define([
  'app/features/dashboard/dashboardSrv'
], function() {
  'use strict';

  describe('dashboardSrv', function() {
    var _dashboardSrv;

    beforeEach(module('grafana.services'));
    beforeEach(module(function($provide) {
      $provide.value('contextSrv', {
      });
    }));

    beforeEach(inject(function(dashboardSrv) {
      _dashboardSrv = dashboardSrv;
    }));

    describe('when creating new dashboard with defaults only', function() {
      var model;

      beforeEach(function() {
        model = _dashboardSrv.create({}, {});
      });

      it('should have title', function() {
        expect(model.title).to.be('No Title');
      });

      it('should have meta', function() {
        expect(model.meta.canSave).to.be(true);
        expect(model.meta.canShare).to.be(true);
      });

      it('should have default properties', function() {
        expect(model.rows.length).to.be(0);
      });
    });

    describe('when getting next panel id', function() {
      var model;

      beforeEach(function() {
        model = _dashboardSrv.create({
          rows: [{ panels: [{ id: 5 }]}]
        });
      });

      it('should return max id + 1', function() {
        expect(model.getNextPanelId()).to.be(6);
      });
    });

    describe('row and panel manipulation', function() {
      var dashboard;

      beforeEach(function() {
        dashboard = _dashboardSrv.create({});
      });

      it('row span should sum spans', function() {
        var spanLeft = dashboard.rowSpan({ panels: [{ span: 2 }, { span: 3 }] });
        expect(spanLeft).to.be(5);
      });

      it('adding default should split span in half', function() {
        dashboard.rows = [{ panels: [{ span: 12, id: 7 }] }];
        dashboard.addPanel({span: 4}, dashboard.rows[0]);

        expect(dashboard.rows[0].panels[0].span).to.be(6);
        expect(dashboard.rows[0].panels[1].span).to.be(6);
        expect(dashboard.rows[0].panels[1].id).to.be(8);
      });

      it('duplicate panel should try to add it to same row', function() {
        var panel = { span: 4, attr: '123', id: 10 };
        dashboard.rows = [{ panels: [panel] }];
        dashboard.duplicatePanel(panel, dashboard.rows[0]);

        expect(dashboard.rows[0].panels[0].span).to.be(4);
        expect(dashboard.rows[0].panels[1].span).to.be(4);
        expect(dashboard.rows[0].panels[1].attr).to.be('123');
        expect(dashboard.rows[0].panels[1].id).to.be(11);
      });

      it('duplicate panel should remove repeat data', function() {
        var panel = { span: 4, attr: '123', id: 10, repeat: 'asd', scopedVars: { test: 'asd' }};
        dashboard.rows = [{ panels: [panel] }];
        dashboard.duplicatePanel(panel, dashboard.rows[0]);

        expect(dashboard.rows[0].panels[1].repeat).to.be(undefined);
        expect(dashboard.rows[0].panels[1].scopedVars).to.be(undefined);
      });

    });

    describe('when creating dashboard with editable false', function() {
      var model;

      beforeEach(function() {
        model = _dashboardSrv.create({
          editable: false
        });
      });

      it('should set editable false', function() {
        expect(model.editable).to.be(false);
      });

    });

    describe('when creating dashboard with old schema', function() {
      var model;
      var graph;
      var singlestat;
      var table;

      beforeEach(function() {
        model = _dashboardSrv.create({
          services: { filter: { time: { from: 'now-1d', to: 'now'}, list: [{}] }},
          pulldowns: [
            {type: 'filtering', enable: true},
            {type: 'annotations', enable: true, annotations: [{name: 'old'}]}
          ],
          rows: [
            {
              panels: [
                {
                  type: 'graph', legend: true, aliasYAxis: { test: 2 },
                  y_formats: ['kbyte', 'ms'],
                  grid: {min: 1, max: 10, rightMin: 5, rightMax: 15, leftLogBase: 1, rightLogBase: 2},
                  leftYAxisLabel: 'left label',
                  targets: [{refId: 'A'}, {}],
                },
                {
                  type: 'singlestat', legend: true, thresholds: '10,20,30', aliasYAxis: { test: 2 }, grid: { min: 1, max: 10 },
                  targets: [{refId: 'A'}, {}],
                },
                {
                  type: 'table', legend: true, styles: [{ thresholds: ["10", "20", "30"]}, { thresholds: ["100", "200", "300"]}],
                  targets: [{refId: 'A'}, {}],
                }
              ]
            }
          ]
        });

        graph = model.rows[0].panels[0];
        singlestat = model.rows[0].panels[1];
        table = model.rows[0].panels[2];
      });

      it('should have title', function() {
        expect(model.title).to.be('No Title');
      });

      it('should have panel id', function() {
        expect(graph.id).to.be(1);
      });

      it('should move time and filtering list', function() {
        expect(model.time.from).to.be('now-1d');
        expect(model.templating.list[0].allFormat).to.be('glob');
      });

      it('graphite panel should change name too graph', function() {
        expect(graph.type).to.be('graph');
      });

      it('single stat panel should have two thresholds', function() {
        expect(singlestat.thresholds).to.be('20,30');
      });

      it('queries without refId should get it', function() {
        expect(graph.targets[1].refId).to.be('B');
      });

      it('update legend setting', function() {
        expect(graph.legend.show).to.be(true);
      });

      it('move aliasYAxis to series override', function() {
        expect(graph.seriesOverrides[0].alias).to.be("test");
        expect(graph.seriesOverrides[0].yaxis).to.be(2);
      });

      it('should move pulldowns to new schema', function() {
        expect(model.annotations.list[0].name).to.be('old');
      });

      it('table panel should only have two thresholds values', function() {
        expect(table.styles[0].thresholds[0]).to.be("20");
        expect(table.styles[0].thresholds[1]).to.be("30");
        expect(table.styles[1].thresholds[0]).to.be("200");
        expect(table.styles[1].thresholds[1]).to.be("300");
      });

      it('graph grid to yaxes options', function() {
        expect(graph.yaxes[0].min).to.be(1);
        expect(graph.yaxes[0].max).to.be(10);
        expect(graph.yaxes[0].format).to.be('kbyte');
        expect(graph.yaxes[0].label).to.be('left label');
        expect(graph.yaxes[0].logBase).to.be(1);
        expect(graph.yaxes[1].min).to.be(5);
        expect(graph.yaxes[1].max).to.be(15);
        expect(graph.yaxes[1].format).to.be('ms');
        expect(graph.yaxes[1].logBase).to.be(2);

        expect(graph.grid.rightMax).to.be(undefined);
        expect(graph.grid.rightLogBase).to.be(undefined);
        expect(graph.y_formats).to.be(undefined);
      });

      it('dashboard schema version should be set to latest', function() {
        expect(model.schemaVersion).to.be(12);
      });

    });

    describe('when creating dashboard model with missing list for annoations or templating', function() {
      var model;

      beforeEach(function() {
        model = _dashboardSrv.create({
          annotations: {
            enable: true,
          },
          templating: {
            enable: true
          }
        });
      });

      it('should add empty list', function() {
        expect(model.annotations.list.length).to.be(0);
        expect(model.templating.list.length).to.be(0);
      });
    });

    describe('Given editable false dashboard', function() {
      var model;

      beforeEach(function() {
        model = _dashboardSrv.create({
          editable:  false,
        });
      });

      it('Should set meta canEdit and canSave to false', function() {
        expect(model.meta.canSave).to.be(false);
        expect(model.meta.canEdit).to.be(false);
      });

      it('getSaveModelClone should remove meta', function() {
        var clone = model.getSaveModelClone();
        expect(clone.meta).to.be(undefined);
      });
    });

    describe('when loading dashboard with old influxdb query schema', function() {
      var model;
      var target;

      beforeEach(function() {
        model = _dashboardSrv.create({
          rows: [{
            panels: [{
              type: 'graph',
              grid: {},
              yaxes: [{}, {}],
              targets: [{
                "alias": "$tag_datacenter $tag_source $col",
                "column": "value",
                "measurement": "logins.count",
                "fields": [
                  {
                    "func": "mean",
                    "name": "value",
                    "mathExpr": "*2",
                    "asExpr": "value"
                  },
                  {
                    "name": "one-minute",
                    "func": "mean",
                    "mathExpr": "*3",
                    "asExpr": "one-minute"
                  }
                ],
                "tags": [],
                "fill": "previous",
                "function": "mean",
                "groupBy": [
                  {
                    "interval": "auto",
                    "type": "time"
                  },
                  {
                    "key": "source",
                    "type": "tag"
                  },
                  {
                    "type": "tag",
                    "key": "datacenter"
                  }
                ],
              }]
            }]
          }]
        });

        target = model.rows[0].panels[0].targets[0];
      });

      it('should update query schema', function() {
        expect(target.fields).to.be(undefined);
        expect(target.select.length).to.be(2);
        expect(target.select[0].length).to.be(4);
        expect(target.select[0][0].type).to.be('field');
        expect(target.select[0][1].type).to.be('mean');
        expect(target.select[0][2].type).to.be('math');
        expect(target.select[0][3].type).to.be('alias');
      });

    });

    describe('when creating dashboard model with missing list for annoations or templating', function() {
      var model;

      beforeEach(function() {
        model = _dashboardSrv.create({
          annotations: {
            enable: true,
          },
          templating: {
            enable: true
          }
        });
      });

      it('should add empty list', function() {
        expect(model.annotations.list.length).to.be(0);
        expect(model.templating.list.length).to.be(0);
      });
    });

    describe('Formatting epoch timestamp when timezone is set as utc', function() {
      var dashboard;

      beforeEach(function() {
        dashboard = _dashboardSrv.create({
          timezone: 'utc',
        });
      });

      it('Should format timestamp with second resolution by default', function() {
        expect(dashboard.formatDate(1234567890000)).to.be('2009-02-13 23:31:30');
      });

      it('Should format timestamp with second resolution even if second format is passed as parameter', function() {
        expect(dashboard.formatDate(1234567890007,'YYYY-MM-DD HH:mm:ss')).to.be('2009-02-13 23:31:30');
      });

      it('Should format timestamp with millisecond resolution if format is passed as parameter', function() {
        expect(dashboard.formatDate(1234567890007,'YYYY-MM-DD HH:mm:ss.SSS')).to.be('2009-02-13 23:31:30.007');
      });
    });

  });
});
