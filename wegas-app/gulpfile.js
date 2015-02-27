/*global require*/

var gulp       = require('gulp'),
    chug       = require('gulp-chug'),
    watch      = require("gulp-watch"),
    uglify     = require("gulp-uglify"),
    minifycss  = require('gulp-cssmin'),
    sourcemaps = require("gulp-sourcemaps"),
    rename     = require("gulp-rename"),
    cache      = require("gulp-cache"),
    replace    = require("gulp-replace");

gulp.task('default', ["submodule", "compress-css", "compress-js"], function() {
    "use strict";
    /*@Hack combo support ...*/
    gulp.src("target/Wegas/**/*-min.js")
        .pipe(replace(/sourceMappingURL=([\.\/]*)map/g, "sourceMappingURL=/map"))
        .pipe(gulp.dest("target/Wegas"));
});
gulp.task("submodule", function() {
    "use strict";
    return gulp.src('target/Wegas/*/gulpfile.js', {readFile: false})
        .pipe(chug());
});
gulp.task("compress-css", function() {
    "use strict";
    return gulp.src(["target/Wegas/**/*.css",
            "!target/Wegas/lib/**",
            "!**/*-min.css"],
        {base: "target/Wegas"})
        .pipe(sourcemaps.init())
        .pipe(rename({suffix: "-min"}))
        .pipe(cache(minifycss()))
        .pipe(sourcemaps.write("map"))

        .pipe(gulp.dest("target/Wegas"));
});
gulp.task("compress-js", function() {
    "use strict";
    return gulp.src(["target/Wegas/**/*.js",
            "!target/Wegas/lib/**",
            "!**/*-min.js",
            "!**/gulpfile.js"],
        {base: "target/Wegas"})
        .pipe(sourcemaps.init())
        .pipe(rename({suffix: "-min"}))
        .pipe(cache(uglify()))
        .pipe(sourcemaps.write("map",
            {
                includeContent: false,
                sourceRoot: "./"
            }))
        .pipe(gulp.dest("target/Wegas"));
});
gulp.task('clear', function(done) {
    "use strict";
    return cache.clearAll(done);
});
gulp.task("watch", function() {
    "use strict";
    return watch("target/Wegas/**/*.hbs", function() {
        return gulp.src('target/Wegas/*/gulpfile.js')
            .pipe(chug({tasks: ["compile-template"]}));
    });
});
