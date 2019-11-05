angular.module('zmon2App').factory('ConfigurationService', ['BootConfig',
  function (BootConfig) {
    return {
      getMinCheckInterval: checkId => {
        if (BootConfig.check.minInterval.whitelistedChecks.indexOf(parseInt(checkId)) >= 0) {
          return BootConfig.check.minInterval.whitelisted;
        }
        return BootConfig.check.minInterval.normal;
      }
    };
  }
]);
