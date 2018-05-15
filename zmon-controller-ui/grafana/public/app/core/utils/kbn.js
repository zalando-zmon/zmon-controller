define([
  'jquery',
  'lodash',
  'moment'
],
function($, _, moment) {
  'use strict';

  var kbn = {};
  kbn.valueFormats = {};

  ///// HELPER FUNCTIONS /////

  kbn.round_interval = function(interval) {
    switch (true) {
    // 0.015s
    case (interval <= 15):
      return 10;      // 0.01s
    // 0.035s
    case (interval <= 35):
      return 20;      // 0.02s
    // 0.075s
    case (interval <= 75):
      return 50;       // 0.05s
    // 0.15s
    case (interval <= 150):
      return 100;      // 0.1s
    // 0.35s
    case (interval <= 350):
      return 200;      // 0.2s
    // 0.75s
    case (interval <= 750):
      return 500;       // 0.5s
    // 1.5s
    case (interval <= 1500):
      return 1000;      // 1s
    // 3.5s
    case (interval <= 3500):
      return 2000;      // 2s
    // 7.5s
    case (interval <= 7500):
      return 5000;      // 5s
    // 12.5s
    case (interval <= 12500):
      return 10000;     // 10s
    // 17.5s
    case (interval <= 17500):
      return 15000;     // 15s
    // 25s
    case (interval <= 25000):
      return 20000;     // 20s
    // 45s
    case (interval <= 45000):
      return 30000;     // 30s
    // 1.5m
    case (interval <= 90000):
      return 60000;     // 1m
    // 3.5m
    case (interval <= 210000):
      return 120000;    // 2m
    // 7.5m
    case (interval <= 450000):
      return 300000;    // 5m
    // 12.5m
    case (interval <= 750000):
      return 600000;    // 10m
    // 12.5m
    case (interval <= 1050000):
      return 900000;    // 15m
    // 25m
    case (interval <= 1500000):
      return 1200000;   // 20m
    // 45m
    case (interval <= 2700000):
      return 1800000;   // 30m
    // 1.5h
    case (interval <= 5400000):
      return 3600000;   // 1h
    // 2.5h
    case (interval <= 9000000):
      return 7200000;   // 2h
    // 4.5h
    case (interval <= 16200000):
      return 10800000;  // 3h
    // 9h
    case (interval <= 32400000):
      return 21600000;  // 6h
    // 24h
    case (interval <= 86400000):
      return 43200000;  // 12h
    // 48h
    case (interval <= 172800000):
      return 86400000;  // 24h
    // 1w
    case (interval <= 604800000):
      return 86400000;  // 24h
    // 3w
    case (interval <= 1814400000):
      return 604800000; // 1w
    // 2y
    case (interval < 3628800000):
      return 2592000000; // 30d
    default:
      return 31536000000; // 1y
    }
  };

  kbn.secondsToHms = function(seconds) {
    var numyears = Math.floor(seconds / 31536000);
    if(numyears){
      return numyears + 'y';
    }
    var numdays = Math.floor((seconds % 31536000) / 86400);
    if(numdays){
      return numdays + 'd';
    }
    var numhours = Math.floor(((seconds % 31536000) % 86400) / 3600);
    if(numhours){
      return numhours + 'h';
    }
    var numminutes = Math.floor((((seconds % 31536000) % 86400) % 3600) / 60);
    if(numminutes){
      return numminutes + 'm';
    }
    var numseconds = Math.floor((((seconds % 31536000) % 86400) % 3600) % 60);
    if(numseconds){
      return numseconds + 's';
    }
    var nummilliseconds = Math.floor(seconds * 1000.0);
    if(nummilliseconds){
      return nummilliseconds + 'ms';
    }

    return 'less then a millisecond'; //'just now' //or other string you like;
  };

  kbn.to_percent = function(number,outof) {
    return Math.floor((number/outof)*10000)/100 + "%";
  };

  kbn.addslashes = function(str) {
    str = str.replace(/\\/g, '\\\\');
    str = str.replace(/\'/g, '\\\'');
    str = str.replace(/\"/g, '\\"');
    str = str.replace(/\0/g, '\\0');
    return str;
  };

  kbn.interval_regex = /(\d+(?:\.\d+)?)(ms|[Mwdhmsy])/;

  // histogram & trends
  kbn.intervals_in_seconds = {
    y: 31536000,
    M: 2592000,
    w: 604800,
    d: 86400,
    h: 3600,
    m: 60,
    s: 1,
    ms: 0.001
  };

  kbn.calculateInterval = function(range, resolution, userInterval) {
    var lowLimitMs = 1; // 1 millisecond default low limit
    var intervalMs, lowLimitInterval;

    if (userInterval) {
      if (userInterval[0] === '>') {
        lowLimitInterval = userInterval.slice(1);
        lowLimitMs = kbn.interval_to_ms(lowLimitInterval);
      }
      else {
        return userInterval;
      }
    }

    intervalMs = kbn.round_interval((range.to.valueOf() - range.from.valueOf()) / resolution);
    if (lowLimitMs > intervalMs) {
      intervalMs = lowLimitMs;
    }

    return kbn.secondsToHms(intervalMs / 1000);
  };

  kbn.describe_interval = function (string) {
    var matches = string.match(kbn.interval_regex);
    if (!matches || !_.has(kbn.intervals_in_seconds, matches[2])) {
      throw new Error('Invalid interval string, expecting a number followed by one of "Mwdhmsy"');
    } else {
      return {
        sec: kbn.intervals_in_seconds[matches[2]],
        type: matches[2],
        count: parseInt(matches[1], 10)
      };
    }
  };

  kbn.interval_to_ms = function(string) {
    var info = kbn.describe_interval(string);
    return info.sec * 1000 * info.count;
  };

  kbn.interval_to_seconds = function (string) {
    var info = kbn.describe_interval(string);
    return info.sec * info.count;
  };

  kbn.query_color_dot = function (color, diameter) {
    return '<div class="icon-circle" style="' + [
      'display:inline-block',
      'color:' + color,
      'font-size:' + diameter + 'px',
    ].join(';') + '"></div>';
  };

  kbn.slugifyForUrl = function(str) {
    return str
      .toLowerCase()
      .replace(/[^\w ]+/g,'')
      .replace(/ +/g,'-');
  };

  kbn.stringToJsRegex = function(str) {
    if (str[0] !== '/') {
      return new RegExp('^' + str + '$');
    }

    var match = str.match(new RegExp('^/(.*?)/(g?i?m?y?)$'));
    return new RegExp(match[1], match[2]);
  };

  kbn.toFixed = function(value, decimals) {
    if (value === null) {
      return "";
    }

    var factor = decimals ? Math.pow(10, Math.max(0, decimals)) : 1;
    var formatted = String(Math.round(value * factor) / factor);

    // if exponent return directly
    if (formatted.indexOf('e') !== -1 || value === 0) {
      return formatted;
    }

    // If tickDecimals was specified, ensure that we have exactly that
    // much precision; otherwise default to the value's own precision.
    if (decimals != null) {
      var decimalPos = formatted.indexOf(".");
      var precision = decimalPos === -1 ? 0 : formatted.length - decimalPos - 1;
      if (precision < decimals) {
        return (precision ? formatted : formatted + ".") + (String(factor)).substr(1, decimals - precision);
      }
    }

    return formatted;
  };

  kbn.toFixedScaled = function(value, decimals, scaledDecimals, additionalDecimals, ext) {
    if (scaledDecimals === null) {
      return kbn.toFixed(value, decimals) + ext;
    } else {
      return kbn.toFixed(value, scaledDecimals + additionalDecimals) + ext;
    }
  };

  kbn.roundValue = function (num, decimals) {
    if (num === null) { return null; }
    var n = Math.pow(10, decimals);
    return Math.round((n * num).toFixed(decimals))  / n;
  };

  ///// FORMAT FUNCTION CONSTRUCTORS /////

  kbn.formatBuilders = {};

  // Formatter which always appends a fixed unit string to the value. No
  // scaling of the value is performed.
  kbn.formatBuilders.fixedUnit = function(unit) {
    return function(size, decimals) {
      if (size === null) { return ""; }
      return kbn.toFixed(size, decimals) + ' ' + unit;
    };
  };

  // Formatter which scales the unit string geometrically according to the given
  // numeric factor. Repeatedly scales the value down by the factor until it is
  // less than the factor in magnitude, or the end of the array is reached.
  kbn.formatBuilders.scaledUnits = function(factor, extArray) {
    return function(size, decimals, scaledDecimals) {
      if (size === null) {
        return "";
      }

      var steps = 0;
      var limit = extArray.length;

      while (Math.abs(size) >= factor) {
        steps++;
        size /= factor;

        if (steps >= limit) { return "NA"; }
      }

      if (steps > 0 && scaledDecimals !== null) {
        decimals = scaledDecimals + (3 * steps);
      }

      return kbn.toFixed(size, decimals) + extArray[steps];
    };
  };

  // Extension of the scaledUnits builder which uses SI decimal prefixes. If an
  // offset is given, it adjusts the starting units at the given prefix; a value
  // of 0 starts at no scale; -3 drops to nano, +2 starts at mega, etc.
  kbn.formatBuilders.decimalSIPrefix = function(unit, offset) {
    var prefixes = ['n', 'µ', 'm', '', 'k', 'M', 'G', 'T', 'P', 'E', 'Z', 'Y'];
    prefixes = prefixes.slice(3 + (offset || 0));
    var units = prefixes.map(function(p) { return ' ' + p + unit; });
    return kbn.formatBuilders.scaledUnits(1000, units);
  };

  // Extension of the scaledUnits builder which uses SI binary prefixes. If
  // offset is given, it starts the units at the given prefix; otherwise, the
  // offset defaults to zero and the initial unit is not prefixed.
  kbn.formatBuilders.binarySIPrefix = function(unit, offset) {
    var prefixes = ['', 'Ki', 'Mi', 'Gi', 'Ti', 'Pi', 'Ei', 'Zi', 'Yi'].slice(offset);
    var units = prefixes.map(function(p) { return ' ' + p + unit; });
    return kbn.formatBuilders.scaledUnits(1024, units);
  };

  // Currency formatter for prefixing a symbol onto a number. Supports scaling
  // up to the trillions.
  kbn.formatBuilders.currency = function(symbol) {
    var units = ['', 'K', 'M', 'B', 'T'];
    var scaler = kbn.formatBuilders.scaledUnits(1000, units);
    return function(size, decimals, scaledDecimals) {
      if (size === null) { return ""; }
      var scaled = scaler(size, decimals, scaledDecimals);
      return symbol + scaled;
    };
  };

  kbn.formatBuilders.simpleCountUnit = function(symbol) {
    var units = ['', 'K', 'M', 'B', 'T'];
    var scaler = kbn.formatBuilders.scaledUnits(1000, units);
    return function(size, decimals, scaledDecimals) {
      if (size === null) { return ""; }
      var scaled = scaler(size, decimals, scaledDecimals);
      return scaled + " " + symbol;
    };
  };

  ///// VALUE FORMATS /////

  // Dimensionless Units
  kbn.valueFormats.none  = kbn.toFixed;
  kbn.valueFormats.short = kbn.formatBuilders.scaledUnits(1000, ['', ' K', ' Mil', ' Bil', ' Tri', ' Quadr', ' Quint', ' Sext', ' Sept']);
  kbn.valueFormats.dB    = kbn.formatBuilders.fixedUnit('dB');
  kbn.valueFormats.ppm   = kbn.formatBuilders.fixedUnit('ppm');

  kbn.valueFormats.percent = function(size, decimals) {
    if (size === null) { return ""; }
    return kbn.toFixed(size, decimals) + '%';
  };

  kbn.valueFormats.percentunit = function(size, decimals) {
    if (size === null) { return ""; }
    return kbn.toFixed(100*size, decimals) + '%';
  };

  // Currencies
  kbn.valueFormats.currencyUSD = kbn.formatBuilders.currency('$');
  kbn.valueFormats.currencyGBP = kbn.formatBuilders.currency('£');
  kbn.valueFormats.currencyEUR = kbn.formatBuilders.currency('€');
  kbn.valueFormats.currencyJPY = kbn.formatBuilders.currency('¥');

  // Data
  kbn.valueFormats.bits   = kbn.formatBuilders.binarySIPrefix('b');
  kbn.valueFormats.bytes  = kbn.formatBuilders.binarySIPrefix('B');
  kbn.valueFormats.kbytes = kbn.formatBuilders.binarySIPrefix('B', 1);
  kbn.valueFormats.mbytes = kbn.formatBuilders.binarySIPrefix('B', 2);
  kbn.valueFormats.gbytes = kbn.formatBuilders.binarySIPrefix('B', 3);

  // Data Rate
  kbn.valueFormats.pps    = kbn.formatBuilders.decimalSIPrefix('pps');
  kbn.valueFormats.bps    = kbn.formatBuilders.decimalSIPrefix('bps');
  kbn.valueFormats.Bps    = kbn.formatBuilders.decimalSIPrefix('Bps');
  kbn.valueFormats.KBs    = kbn.formatBuilders.decimalSIPrefix('Bs', 1);
  kbn.valueFormats.Kbits  = kbn.formatBuilders.decimalSIPrefix('bits', 1);
  kbn.valueFormats.MBs    = kbn.formatBuilders.decimalSIPrefix('Bs', 2);
  kbn.valueFormats.Mbits  = kbn.formatBuilders.decimalSIPrefix('bits', 2);
  kbn.valueFormats.GBs    = kbn.formatBuilders.decimalSIPrefix('Bs', 3);
  kbn.valueFormats.Gbits  = kbn.formatBuilders.decimalSIPrefix('bits', 3);

  // Throughput
  kbn.valueFormats.ops  = kbn.formatBuilders.simpleCountUnit('ops');
  kbn.valueFormats.rps  = kbn.formatBuilders.simpleCountUnit('rps');
  kbn.valueFormats.wps  = kbn.formatBuilders.simpleCountUnit('wps');
  kbn.valueFormats.iops = kbn.formatBuilders.simpleCountUnit('iops');

  // Energy
  kbn.valueFormats.watt         = kbn.formatBuilders.decimalSIPrefix('W');
  kbn.valueFormats.kwatt        = kbn.formatBuilders.decimalSIPrefix('W', 1);
  kbn.valueFormats.voltamp      = kbn.formatBuilders.decimalSIPrefix('VA');
  kbn.valueFormats.kvoltamp     = kbn.formatBuilders.decimalSIPrefix('VA', 1);
  kbn.valueFormats.voltampreact = kbn.formatBuilders.decimalSIPrefix('var');
  kbn.valueFormats.watth        = kbn.formatBuilders.decimalSIPrefix('Wh');
  kbn.valueFormats.kwatth       = kbn.formatBuilders.decimalSIPrefix('Wh', 1);
  kbn.valueFormats.joule        = kbn.formatBuilders.decimalSIPrefix('J');
  kbn.valueFormats.ev           = kbn.formatBuilders.decimalSIPrefix('eV');
  kbn.valueFormats.amp          = kbn.formatBuilders.decimalSIPrefix('A');
  kbn.valueFormats.volt         = kbn.formatBuilders.decimalSIPrefix('V');
  kbn.valueFormats.dBm          = kbn.formatBuilders.decimalSIPrefix('dBm');

  // Temperature
  kbn.valueFormats.celsius   = kbn.formatBuilders.fixedUnit('°C');
  kbn.valueFormats.farenheit = kbn.formatBuilders.fixedUnit('°F');
  kbn.valueFormats.kelvin    = kbn.formatBuilders.fixedUnit('K');
  kbn.valueFormats.humidity  = kbn.formatBuilders.fixedUnit('%H');

  // Pressure
  kbn.valueFormats.pressurembar = kbn.formatBuilders.fixedUnit('mbar');
  kbn.valueFormats.pressurehpa  = kbn.formatBuilders.fixedUnit('hPa');
  kbn.valueFormats.pressurehg   = kbn.formatBuilders.fixedUnit('"Hg');
  kbn.valueFormats.pressurepsi  = kbn.formatBuilders.scaledUnits(1000, [' psi', ' ksi', ' Mpsi']);

  // Length
  kbn.valueFormats.lengthm  = kbn.formatBuilders.decimalSIPrefix('m');
  kbn.valueFormats.lengthmm = kbn.formatBuilders.decimalSIPrefix('m', -1);
  kbn.valueFormats.lengthkm = kbn.formatBuilders.decimalSIPrefix('m', 1);
  kbn.valueFormats.lengthmi = kbn.formatBuilders.fixedUnit('mi');

  // Velocity
  kbn.valueFormats.velocityms   = kbn.formatBuilders.fixedUnit('m/s');
  kbn.valueFormats.velocitykmh  = kbn.formatBuilders.fixedUnit('km/h');
  kbn.valueFormats.velocitymph  = kbn.formatBuilders.fixedUnit('mph');
  kbn.valueFormats.velocityknot = kbn.formatBuilders.fixedUnit('kn');

  // Volume
  kbn.valueFormats.litre  = kbn.formatBuilders.decimalSIPrefix('L');
  kbn.valueFormats.mlitre = kbn.formatBuilders.decimalSIPrefix('L', -1);
  kbn.valueFormats.m3     = kbn.formatBuilders.decimalSIPrefix('m3');

  // Time
  kbn.valueFormats.hertz = kbn.formatBuilders.decimalSIPrefix('Hz');

  kbn.valueFormats.ms = function(size, decimals, scaledDecimals) {
    if (size === null) { return ""; }

    if (Math.abs(size) < 1000) {
      return kbn.toFixed(size, decimals) + " ms";
    }
    // Less than 1 min
    else if (Math.abs(size) < 60000) {
      return kbn.toFixedScaled(size / 1000, decimals, scaledDecimals, 3, " s");
    }
    // Less than 1 hour, devide in minutes
    else if (Math.abs(size) < 3600000) {
      return kbn.toFixedScaled(size / 60000, decimals, scaledDecimals, 5, " min");
    }
    // Less than one day, devide in hours
    else if (Math.abs(size) < 86400000) {
      return kbn.toFixedScaled(size / 3600000, decimals, scaledDecimals, 7, " hour");
    }
    // Less than one year, devide in days
    else if (Math.abs(size) < 31536000000) {
      return kbn.toFixedScaled(size / 86400000, decimals, scaledDecimals, 8, " day");
    }

    return kbn.toFixedScaled(size / 31536000000, decimals, scaledDecimals, 10, " year");
  };

  kbn.valueFormats.s = function(size, decimals, scaledDecimals) {
    if (size === null) { return ""; }

    if (Math.abs(size) < 60) {
      return kbn.toFixed(size, decimals) + " s";
    }
    // Less than 1 hour, devide in minutes
    else if (Math.abs(size) < 3600) {
      return kbn.toFixedScaled(size / 60, decimals, scaledDecimals, 1, " min");
    }
    // Less than one day, devide in hours
    else if (Math.abs(size) < 86400) {
      return kbn.toFixedScaled(size / 3600, decimals, scaledDecimals, 4, " hour");
    }
    // Less than one week, devide in days
    else if (Math.abs(size) < 604800) {
      return kbn.toFixedScaled(size / 86400, decimals, scaledDecimals, 5, " day");
    }
    // Less than one year, devide in week
    else if (Math.abs(size) < 31536000) {
      return kbn.toFixedScaled(size / 604800, decimals, scaledDecimals, 6, " week");
    }

    return kbn.toFixedScaled(size / 3.15569e7, decimals, scaledDecimals, 7, " year");
  };

  kbn.valueFormats['µs'] = function(size, decimals, scaledDecimals) {
    if (size === null) { return ""; }

    if (Math.abs(size) < 1000) {
      return kbn.toFixed(size, decimals) + " µs";
    }
    else if (Math.abs(size) < 1000000) {
      return kbn.toFixedScaled(size / 1000, decimals, scaledDecimals, 3, " ms");
    }
    else {
      return kbn.toFixedScaled(size / 1000000, decimals, scaledDecimals, 6, " s");
    }
  };

  kbn.valueFormats.ns = function(size, decimals, scaledDecimals) {
    if (size === null) { return ""; }

    if (Math.abs(size) < 1000) {
      return kbn.toFixed(size, decimals) + " ns";
    }
    else if (Math.abs(size) < 1000000) {
      return kbn.toFixedScaled(size / 1000, decimals, scaledDecimals, 3, " µs");
    }
    else if (Math.abs(size) < 1000000000) {
      return kbn.toFixedScaled(size / 1000000, decimals, scaledDecimals, 6, " ms");
    }
    else if (Math.abs(size) < 60000000000){
      return kbn.toFixedScaled(size / 1000000000, decimals, scaledDecimals, 9, " s");
    }
    else {
      return kbn.toFixedScaled(size / 60000000000, decimals, scaledDecimals, 12, " min");
    }
  };

  kbn.valueFormats.m = function(size, decimals, scaledDecimals) {
    if (size === null) { return ""; }

    if (Math.abs(size) < 60) {
      return kbn.toFixed(size, decimals) + " min";
    }
    else if (Math.abs(size) < 1440) {
      return kbn.toFixedScaled(size / 60, decimals, scaledDecimals, 2, " hour");
    }
    else if (Math.abs(size) < 10080) {
      return kbn.toFixedScaled(size / 1440, decimals, scaledDecimals, 3, " day");
    }
    else if (Math.abs(size) < 604800) {
      return kbn.toFixedScaled(size / 10080, decimals, scaledDecimals, 4, " week");
    }
    else {
      return kbn.toFixedScaled(size / 5.25948e5, decimals, scaledDecimals, 5, " year");
    }
  };

  kbn.valueFormats.h = function(size, decimals, scaledDecimals) {
    if (size === null) { return ""; }

    if (Math.abs(size) < 24) {
      return kbn.toFixed(size, decimals) + " hour";
    }
    else if (Math.abs(size) < 168) {
      return kbn.toFixedScaled(size / 24, decimals, scaledDecimals, 2, " day");
    }
    else if (Math.abs(size) < 8760) {
      return kbn.toFixedScaled(size / 168, decimals, scaledDecimals, 3, " week");
    }
    else {
      return kbn.toFixedScaled(size / 8760, decimals, scaledDecimals, 4, " year");
    }
  };

  kbn.valueFormats.d = function(size, decimals, scaledDecimals) {
    if (size === null) { return ""; }

    if (Math.abs(size) < 7) {
      return kbn.toFixed(size, decimals) + " day";
    }
    else if (Math.abs(size) < 365) {
      return kbn.toFixedScaled(size / 7, decimals, scaledDecimals, 2, " week");
    }
    else {
      return kbn.toFixedScaled(size / 365, decimals, scaledDecimals, 3, " year");
    }
  };

  kbn.toDuration = function(size, timeScale) {
    return moment.duration(size, timeScale);
  };

  kbn.valueFormats.dtdurationms = function(size) {
    return kbn.toDuration(size, 'ms').humanize();
  };

  kbn.valueFormats.dtdurations = function(size) {
    return kbn.toDuration(size, 's').humanize();
  };

  ///// FORMAT MENU /////

  kbn.getUnitFormats = function() {
    return [
      {
        text: 'none',
        submenu: [
          {text: 'none' ,             value: 'none'       },
          {text: 'short',             value: 'short'      },
          {text: 'percent (0-100)',   value: 'percent'    },
          {text: 'percent (0.0-1.0)', value: 'percentunit'},
          {text: 'Humidity (%H)',     value: 'humidity'   },
          {text: 'ppm',               value: 'ppm'        },
          {text: 'decibel',           value: 'dB'         },
        ]
      },
      {
        text: 'currency',
        submenu: [
          {text: 'Dollars ($)', value: 'currencyUSD'},
          {text: 'Pounds (£)',  value: 'currencyGBP'},
          {text: 'Euro (€)',    value: 'currencyEUR'},
          {text: 'Yen (¥)',     value: 'currencyJPY'},
        ]
      },
      {
        text: 'time',
        submenu: [
          {text: 'Hertz (1/s)',       value: 'hertz'},
          {text: 'nanoseconds (ns)' , value: 'ns'   },
          {text: 'microseconds (µs)', value: 'µs'   },
          {text: 'milliseconds (ms)', value: 'ms'   },
          {text: 'seconds (s)',       value: 's'    },
          {text: 'minutes (m)',       value: 'm'    },
          {text: 'hours (h)',         value: 'h'    },
          {text: 'days (d)',          value: 'd'    },
          {text: 'duration (ms)',     value: 'dtdurationms' },
          {text: 'duration (s)',      value: 'dtdurations' }
        ]
      },
      {
        text: 'data',
        submenu: [
          {text: 'bits',      value: 'bits'  },
          {text: 'bytes',     value: 'bytes' },
          {text: 'kilobytes', value: 'kbytes'},
          {text: 'megabytes', value: 'mbytes'},
          {text: 'gigabytes', value: 'gbytes'},
        ]
      },
      {
        text: 'data rate',
        submenu: [
          {text: 'packets/sec', value: 'pps'},
          {text: 'bits/sec',    value: 'bps'},
          {text: 'bytes/sec',   value: 'Bps'},
          {text: 'kilobits/sec', value: 'Kbits'},
          {text: 'kilobytes/sec',    value: 'KBs'},
          {text: 'megabits/sec', value: 'Mbits'},
          {text: 'megabytes/sec',    value: 'MBs'},
          {text: 'gigabytes/sec',   value: 'GBs'},
          {text: 'gigabits/sec',   value: 'Gbits'},
        ]
      },
      {
        text: 'throughput',
        submenu: [
          {text: 'ops/sec (ops)',       value: 'ops' },
          {text: 'reads/sec (rps)',     value: 'rps' },
          {text: 'writes/sec (wps)',    value: 'wps' },
          {text: 'I/O ops/sec (iops)',  value: 'iops'},
        ]
      },
      {
        text: 'length',
        submenu: [
          {text: 'millimetre (mm)', value: 'lengthmm'},
          {text: 'meter (m)',       value: 'lengthm' },
          {text: 'kilometer (km)',  value: 'lengthkm'},
          {text: 'mile (mi)',       value: 'lengthmi'},
        ]
      },
      {
        text: 'velocity',
        submenu: [
          {text: 'm/s',       value: 'velocityms'  },
          {text: 'km/h',      value: 'velocitykmh' },
          {text: 'mph',       value: 'velocitymph' },
          {text: 'knot (kn)', value: 'velocityknot'},
        ]
      },
      {
        text: 'volume',
        submenu: [
          {text: 'millilitre',  value: 'mlitre'},
          {text: 'litre',       value: 'litre' },
          {text: 'cubic metre', value: 'm3'    },
        ]
      },
      {
        text: 'energy',
        submenu: [
          {text: 'watt (W)',                   value: 'watt'        },
          {text: 'kilowatt (kW)',              value: 'kwatt'       },
          {text: 'volt-ampere (VA)',           value: 'voltamp'     },
          {text: 'kilovolt-ampere (kVA)',      value: 'kvoltamp'    },
          {text: 'volt-ampere reactive (var)', value: 'voltampreact'},
          {text: 'watt-hour (Wh)',             value: 'watth'       },
          {text: 'kilowatt-hour (kWh)',        value: 'kwatth'      },
          {text: 'joule (J)',                  value: 'joule'       },
          {text: 'electron volt (eV)',         value: 'ev'          },
          {text: 'Ampere (A)',                 value: 'amp'         },
          {text: 'Volt (V)',                   value: 'volt'        },
          {text: 'Decibel-milliwatt (dBm)',    value: 'dBm'         },
        ]
      },
      {
        text: 'temperature',
        submenu: [
          {text: 'Celcius (°C)',    value: 'celsius'     },
          {text: 'Farenheit (°F)',  value: 'farenheit'   },
          {text: 'Kelvin (K)',      value: 'kelvin'      },
        ]
      },
      {
        text: 'pressure',
        submenu: [
          {text: 'Millibars',         value: 'pressurembar'},
          {text: 'Hectopascals',      value: 'pressurehpa' },
          {text: 'Inches of mercury', value: 'pressurehg'  },
          {text: 'PSI',               value: 'pressurepsi' },
        ]
      }
    ];
  };

  return kbn;
});
