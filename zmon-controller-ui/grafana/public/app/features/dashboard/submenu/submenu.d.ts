/// <reference path="../../../../../public/app/headers/common.d.ts" />
export declare class SubmenuCtrl {
    private $rootScope;
    private templateValuesSrv;
    private dynamicDashboardSrv;
    annotations: any;
    variables: any;
    dashboard: any;
    /** @ngInject */
    constructor($rootScope: any, templateValuesSrv: any, dynamicDashboardSrv: any);
    disableAnnotation(annotation: any): void;
    getValuesForTag(variable: any, tagKey: any): any;
    variableUpdated(variable: any): void;
}
export declare function submenuDirective(): {
    restrict: string;
    templateUrl: string;
    controller: typeof SubmenuCtrl;
    bindToController: boolean;
    controllerAs: string;
    scope: {
        dashboard: string;
    };
};
