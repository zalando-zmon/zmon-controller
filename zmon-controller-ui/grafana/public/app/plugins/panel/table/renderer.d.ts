/// <reference path="../../../../../public/app/headers/common.d.ts" />
export declare class TableRenderer {
    private panel;
    private table;
    private isUtc;
    formaters: any[];
    colorState: any;
    constructor(panel: any, table: any, isUtc: any);
    getColorForValue(value: any, style: any): any;
    defaultCellFormater(v: any): any;
    createColumnFormater(style: any, column: any): (v: any) => any;
    formatColumnValue(colIndex: any, value: any): any;
    renderCell(columnIndex: any, value: any, addWidthHack?: boolean): string;
    render(page: any): string;
}
