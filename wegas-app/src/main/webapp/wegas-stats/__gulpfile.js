/*
 * Wegas
 * http://wegas.albasim.ch
 *
 * Copyright (c) 2015 School of Business and Engineering Vaud, Comem
 * Licensed under the MIT License
 */

/*global require*/
var gulp = require('gulp');
var webpack = require('webpack');
var config = require('./webpack.config.prod');
gulp.task("default", ["webpack"], function() {
    "use strict";
});

gulp.task("webpack", function(callback) {
    "use strict";
    webpack(config, function(err, stats) {
        console.log("[webpack]", stats.toString({color: true}));
        callback();
    });
});


