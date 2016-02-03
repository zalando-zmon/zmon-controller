export declare class TableModel {
    columns: any[];
    rows: any[];
    constructor();
    sort(options: any): void;
    static transform(data: any, panel: any): TableModel;
}
