///<reference path="../../../headers/common.d.ts" />
System.register(['lodash', 'angular', 'moment', 'app/core/utils/rangeutil', './input_date'], function(exports_1) {
    var lodash_1, angular_1, moment_1, rangeUtil, input_date_1;
    var TimePickerCtrl;
    function settingsDirective() {
        return {
            restrict: 'E',
            templateUrl: 'public/app/features/dashboard/timepicker/settings.html',
            controller: TimePickerCtrl,
            bindToController: true,
            controllerAs: 'ctrl',
            scope: {
                dashboard: "="
            }
        };
    }
    exports_1("settingsDirective", settingsDirective);
    function timePickerDirective() {
        return {
            restrict: 'E',
            templateUrl: 'public/app/features/dashboard/timepicker/timepicker.html',
            controller: TimePickerCtrl,
            bindToController: true,
            controllerAs: 'ctrl',
            scope: {
                dashboard: "="
            }
        };
    }
    exports_1("timePickerDirective", timePickerDirective);
    return {
        setters:[
            function (lodash_1_1) {
                lodash_1 = lodash_1_1;
            },
            function (angular_1_1) {
                angular_1 = angular_1_1;
            },
            function (moment_1_1) {
                moment_1 = moment_1_1;
            },
            function (rangeUtil_1) {
                rangeUtil = rangeUtil_1;
            },
            function (input_date_1_1) {
                input_date_1 = input_date_1_1;
            }],
        execute: function() {
            TimePickerCtrl = (function () {
                /** @ngInject */
                function TimePickerCtrl($scope, $rootScope, timeSrv) {
                    var _this = this;
                    this.$scope = $scope;
                    this.$rootScope = $rootScope;
                    this.timeSrv = timeSrv;
                    $scope.ctrl = this;
                    $rootScope.onAppEvent('zoom-out', function () { return _this.zoom(2); }, $scope);
                    $rootScope.onAppEvent('shift-time-forward', function () { return _this.move(1); }, $scope);
                    $rootScope.onAppEvent('shift-time-backward', function () { return _this.move(-1); }, $scope);
                    $rootScope.onAppEvent('refresh', function () { return _this.init(); }, $scope);
                    $rootScope.onAppEvent('dash-editor-hidden', function () { return _this.isOpen = false; }, $scope);
                    this.init();
                }
                TimePickerCtrl.prototype.init = function () {
                    this.panel = this.dashboard.timepicker;
                    lodash_1.default.defaults(this.panel, TimePickerCtrl.defaults);
                    var time = angular_1.default.copy(this.timeSrv.timeRange());
                    var timeRaw = angular_1.default.copy(this.timeSrv.timeRange(false));
                    if (!this.dashboard.isTimezoneUtc()) {
                        time.from.local();
                        time.to.local();
                        if (moment_1.default.isMoment(timeRaw.from)) {
                            timeRaw.from.local();
                        }
                        if (moment_1.default.isMoment(timeRaw.to)) {
                            timeRaw.to.local();
                        }
                    }
                    else {
                        this.isUtc = true;
                    }
                    this.rangeString = rangeUtil.describeTimeRange(timeRaw);
                    this.absolute = { fromJs: time.from.toDate(), toJs: time.to.toDate() };
                    this.tooltip = this.dashboard.formatDate(time.from) + ' <br>to<br>';
                    this.tooltip += this.dashboard.formatDate(time.to);
                    // do not update time raw when dropdown is open
                    // as auto refresh will reset the from/to input fields
                    if (!this.isOpen) {
                        this.timeRaw = timeRaw;
                    }
                };
                TimePickerCtrl.prototype.zoom = function (factor) {
                    var range = this.timeSrv.timeRange();
                    var timespan = (range.to.valueOf() - range.from.valueOf());
                    var center = range.to.valueOf() - timespan / 2;
                    var to = (center + (timespan * factor) / 2);
                    var from = (center - (timespan * factor) / 2);
                    if (to > Date.now() && range.to <= Date.now()) {
                        var offset = to - Date.now();
                        from = from - offset;
                        to = Date.now();
                    }
                    this.timeSrv.setTime({ from: moment_1.default.utc(from), to: moment_1.default.utc(to) });
                };
                TimePickerCtrl.prototype.move = function (direction) {
                    var range = this.timeSrv.timeRange();
                    var timespan = (range.to.valueOf() - range.from.valueOf()) / 2;
                    var to, from;
                    if (direction === -1) {
                        to = range.to.valueOf() - timespan;
                        from = range.from.valueOf() - timespan;
                    }
                    else if (direction === 1) {
                        to = range.to.valueOf() + timespan;
                        from = range.from.valueOf() + timespan;
                        if (to > Date.now() && range.to < Date.now()) {
                            to = Date.now();
                            from = range.from.valueOf();
                        }
                    }
                    else {
                        to = range.to.valueOf();
                        from = range.from.valueOf();
                    }
                    this.timeSrv.setTime({ from: moment_1.default.utc(from), to: moment_1.default.utc(to) });
                };
                TimePickerCtrl.prototype.openDropdown = function () {
                    this.init();
                    this.isOpen = true;
                    this.timeOptions = rangeUtil.getRelativeTimesList(this.panel, this.rangeString);
                    this.refresh = {
                        value: this.dashboard.refresh,
                        options: lodash_1.default.map(this.panel.refresh_intervals, function (interval) {
                            return { text: interval, value: interval };
                        })
                    };
                    this.refresh.options.unshift({ text: 'off' });
                    this.$rootScope.appEvent('show-dash-editor', {
                        src: 'public/app/features/dashboard/timepicker/dropdown.html',
                        scope: this.$scope,
                        cssClass: 'gf-timepicker-dropdown',
                    });
                };
                TimePickerCtrl.prototype.applyCustom = function () {
                    if (this.refresh.value !== this.dashboard.refresh) {
                        this.timeSrv.setAutoRefresh(this.refresh.value);
                    }
                    this.timeSrv.setTime(this.timeRaw, true);
                    this.$rootScope.appEvent('hide-dash-editor');
                };
                TimePickerCtrl.prototype.absoluteFromChanged = function () {
                    this.timeRaw.from = this.getAbsoluteMomentForTimezone(this.absolute.fromJs);
                };
                TimePickerCtrl.prototype.absoluteToChanged = function () {
                    this.timeRaw.to = this.getAbsoluteMomentForTimezone(this.absolute.toJs);
                };
                TimePickerCtrl.prototype.getAbsoluteMomentForTimezone = function (jsDate) {
                    return this.dashboard.isTimezoneUtc() ? moment_1.default(jsDate).utc() : moment_1.default(jsDate);
                };
                TimePickerCtrl.prototype.setRelativeFilter = function (timespan) {
                    var range = { from: timespan.from, to: timespan.to };
                    if (this.panel.nowDelay && range.to === 'now') {
                        range.to = 'now-' + this.panel.nowDelay;
                    }
                    this.timeSrv.setTime(range);
                    this.$rootScope.appEvent('hide-dash-editor');
                };
                TimePickerCtrl.tooltipFormat = 'MMM D, YYYY HH:mm:ss';
                TimePickerCtrl.defaults = {
                    time_options: ['5m', '15m', '1h', '6h', '12h', '24h', '2d', '7d', '30d'],
                    refresh_intervals: ['5s', '10s', '30s', '1m', '5m', '15m', '30m', '1h', '2h', '1d'],
                };
                return TimePickerCtrl;
            })();
            exports_1("TimePickerCtrl", TimePickerCtrl);
            angular_1.default.module('grafana.directives').directive('gfTimePickerSettings', settingsDirective);
            angular_1.default.module('grafana.directives').directive('gfTimePicker', timePickerDirective);
            angular_1.default.module("grafana.directives").directive('inputDatetime', input_date_1.inputDateDirective);
        }
    }
});
//# sourceMappingURL=timepicker.js.map