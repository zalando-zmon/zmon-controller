System.register(['../playlist_edit_ctrl', 'test/lib/common'], function(exports_1) {
    var common_1, playlist_edit_ctrl_1;
    return {
        setters:[
            function (playlist_edit_ctrl_1_1) {
                playlist_edit_ctrl_1 = playlist_edit_ctrl_1_1;
            },
            function (common_1_1) {
                common_1 = common_1_1;
            }],
        execute: function() {
            common_1.describe('PlaylistEditCtrl', function () {
                var ctx;
                common_1.beforeEach(function () {
                    ctx = new playlist_edit_ctrl_1.PlaylistEditCtrl(null, null, null, null, { current: { params: {} } });
                    ctx.dashboardresult = [
                        { id: 2, title: 'dashboard: 2' },
                        { id: 3, title: 'dashboard: 3' }
                    ];
                    ctx.tagresult = [
                        { term: 'graphite', count: 1 },
                        { term: 'nyc', count: 2 }
                    ];
                });
                common_1.describe('searchresult returns 2 dashboards, ', function () {
                    common_1.it('found dashboard should be 2', function () {
                        common_1.expect(ctx.dashboardresult.length).to.be(2);
                    });
                    common_1.it('filtred result should be 2', function () {
                        ctx.filterFoundPlaylistItems();
                        common_1.expect(ctx.filteredDashboards.length).to.be(2);
                        common_1.expect(ctx.filteredTags.length).to.be(2);
                    });
                    common_1.describe('adds one dashboard to playlist, ', function () {
                        common_1.beforeEach(function () {
                            ctx.addPlaylistItem({ id: 2, title: 'dashboard: 2' });
                            ctx.addTagPlaylistItem({ term: 'graphite' });
                            ctx.filterFoundPlaylistItems();
                        });
                        common_1.it('playlistitems should be increased by one', function () {
                            common_1.expect(ctx.playlistItems.length).to.be(2);
                        });
                        common_1.it('filtred playlistitems should be reduced by one', function () {
                            common_1.expect(ctx.filteredDashboards.length).to.be(1);
                            common_1.expect(ctx.filteredTags.length).to.be(1);
                        });
                        common_1.it('found dashboard should be 2', function () {
                            common_1.expect(ctx.dashboardresult.length).to.be(2);
                        });
                        common_1.describe('removes one dashboard from playlist, ', function () {
                            common_1.beforeEach(function () {
                                ctx.removePlaylistItem(ctx.playlistItems[0]);
                                ctx.removePlaylistItem(ctx.playlistItems[0]);
                                ctx.filterFoundPlaylistItems();
                            });
                            common_1.it('playlistitems should be increased by one', function () {
                                common_1.expect(ctx.playlistItems.length).to.be(0);
                            });
                            common_1.it('found dashboard should be 2', function () {
                                common_1.expect(ctx.dashboardresult.length).to.be(2);
                                common_1.expect(ctx.filteredDashboards.length).to.be(2);
                                common_1.expect(ctx.filteredTags.length).to.be(2);
                                common_1.expect(ctx.tagresult.length).to.be(2);
                            });
                        });
                    });
                });
            });
        }
    }
});
//# sourceMappingURL=playlist_edit_ctrl_specs.js.map