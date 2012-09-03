/*
YUI 3.6.0 (build 5521)
Copyright 2012 Yahoo! Inc. All rights reserved.
Licensed under the BSD License.
http://yuilibrary.com/license/
*/
YUI.add("matrix",function(c){var a={_rounder:100000,_round:function(d){d=Math.round(d*a._rounder)/a._rounder;return d;},rad2deg:function(d){var e=d*(180/Math.PI);return e;},deg2rad:function(e){var d=e*(Math.PI/180);return d;},angle2rad:function(d){if(typeof d==="string"&&d.indexOf("rad")>-1){d=parseFloat(d);}else{d=a.deg2rad(parseFloat(d));}return d;},convertTransformToArray:function(d){var e=[[d.a,d.c,d.dx],[d.b,d.d,d.dy],[0,0,1]];return e;},getDeterminant:function(f){var e=0,d=f.length,g=0,h;if(d==2){return f[0][0]*f[1][1]-f[0][1]*f[1][0];}for(;g<d;++g){h=f[g][0];if(g%2===0||g===0){e+=h*a.getDeterminant(a.getMinors(f,g,0));}else{e-=h*a.getDeterminant(a.getMinors(f,g,0));}}return e;},inverse:function(g){var f=0,d=g.length,l=0,k,e,h=[],m=[];if(d===2){f=g[0][0]*g[1][1]-g[0][1]*g[1][0];e=[[g[1][1]*f,-g[1][0]*f],[-g[0][1]*f,g[0][0]*f]];}else{f=a.getDeterminant(g);for(;l<d;++l){h[l]=[];for(k=0;k<d;++k){m=a.getMinors(g,k,l);h[l][k]=a.getDeterminant(m);if((l+k)%2!==0&&(l+k)!==0){h[l][k]*=-1;}}}e=a.scalarMultiply(h,1/f);}return e;},scalarMultiply:function(e,h){var g=0,f,d=e.length;for(;g<d;++g){for(f=0;f<d;++f){e[g][f]=a._round(e[g][f]*h);}}return e;},transpose:function(e){var d=e.length,g=0,f=0,h=[];for(;g<d;++g){h[g]=[];for(f=0;f<d;++f){h[g].push(e[f][g]);}}return h;},getMinors:function(e,l,m){var h=[],d=e.length,g=0,f,k;for(;g<d;++g){if(g!==l){k=[];for(f=0;f<d;++f){if(f!==m){k.push(e[g][f]);}}h.push(k);}}return h;},sign:function(d){return d===0?1:d/Math.abs(d);},vectorMatrixProduct:function(f,g){var k,h,e=f.length,l=[],d;for(k=0;k<e;++k){d=0;for(h=0;h<e;++h){d+=f[k]*g[k][h];}l[k]=d;}return l;},decompose:function(l){var m=parseFloat(l[0][0]),j=parseFloat(l[1][0]),h=parseFloat(l[0][1]),g=parseFloat(l[1][1]),o=parseFloat(l[0][2]),n=parseFloat(l[1][2]),f,k,i,e;if((m*g-j*h)===0){return false;}k=a._round(Math.sqrt(m*m+j*j));m/=k;j/=k;e=a._round(m*h+j*g);h-=m*e;g-=j*e;i=a._round(Math.sqrt(h*h+g*g));h/=i;g/=i;e/=i;e=a._round(a.rad2deg(Math.atan(e)));f=a._round(a.rad2deg(Math.atan2(l[1][0],l[0][0])));return[["translate",o,n],["rotate",f],["skewX",e],["scale",k,i]];},getTransformArray:function(g){var j=/\s*([a-z]*)\(([\w,\.,\-,\s]*)\)/gi,i=[],f,d,h,e=a.transformMethods;while((d=j.exec(g))){if(e.hasOwnProperty(d[1])){f=d[2].split(",");f.unshift(d[1]);i.push(f);}else{if(d[1]=="matrix"){f=d[2].split(",");h=a.decompose([[f[0],f[2],f[4]],[f[1],f[3],f[5]],[0,0,1]]);i.push(h[0]);i.push(h[1]);i.push(h[2]);i.push(h[3]);}}}return i;},getTransformFunctionArray:function(d){var e;switch(d){case"skew":e=[d,0,0];break;case"scale":e=[d,1,1];break;case"scaleX":e=[d,1];break;case"scaleY":e=[d,1];break;case"translate":e=[d,0,0];break;default:e=[d,0];break;}return e;},compareTransformSequence:function(e,j){var g=0,d=e.length,f=j.length,h=d===f;if(h){for(;g<d;++g){if(e[g][0]!=j[g][0]){h=false;break;}}}return h;},transformMethods:{rotate:"rotate",skew:"skew",skewX:"skewX",skewY:"skewY",translate:"translate",translateX:"translateX",translateY:"tranlsateY",scale:"scale",scaleX:"scaleX",scaleY:"scaleY"}};c.MatrixUtil=a;var b=function(d){this.init(d);};b.prototype={_rounder:100000,multiply:function(m,k,j,i,q,p){var l=this,h=l.a*m+l.c*k,g=l.b*m+l.d*k,f=l.a*j+l.c*i,e=l.b*j+l.d*i,o=l.a*q+l.c*p+l.dx,n=l.b*q+l.d*p+l.dy;l.a=this._round(h);l.b=this._round(g);l.c=this._round(f);l.d=this._round(e);l.dx=this._round(o);l.dy=this._round(n);return this;},applyCSSText:function(g){var f=/\s*([a-z]*)\(([\w,\.,\-,\s]*)\)/gi,e,d;g=g.replace(/matrix/g,"multiply");while((d=f.exec(g))){if(typeof this[d[1]]==="function"){e=d[2].split(",");this[d[1]].apply(this,e);}}},getTransformArray:function(h){var g=/\s*([a-z]*)\(([\w,\.,\-,\s]*)\)/gi,f=[],e,d;h=h.replace(/matrix/g,"multiply");while((d=g.exec(h))){if(typeof this[d[1]]==="function"){e=d[2].split(",");e.unshift(d[1]);f.push(e);}}return f;},_defaults:{a:1,b:0,c:0,d:1,dx:0,dy:0},_round:function(d){d=Math.round(d*this._rounder)/this._rounder;return d;},init:function(d){var e=this._defaults,f;d=d||{};for(f in e){if(e.hasOwnProperty(f)){this[f]=(f in d)?d[f]:e[f];}}this._config=d;},scale:function(d,e){this.multiply(d,0,0,e,0,0);return this;},skew:function(d,e){d=d||0;e=e||0;if(d!==undefined){d=Math.tan(this.angle2rad(d));}if(e!==undefined){e=Math.tan(this.angle2rad(e));}this.multiply(1,e,d,1,0,0);return this;},skewX:function(d){this.skew(d);return this;},skewY:function(d){this.skew(null,d);return this;},toCSSText:function(){var f=this,e=f.dx,d=f.dy,g="matrix(";if(c.UA.gecko){if(!isNaN(e)){e+="px";}if(!isNaN(d)){d+="px";}}g+=f.a+","+f.b+","+f.c+","+f.d+","+e+","+d;g+=")";return g;},toFilterText:function(){var d=this,e="progid:DXImageTransform.Microsoft.Matrix(";e+="M11="+d.a+","+"M21="+d.b+","+"M12="+d.c+","+"M22="+d.d+","+'sizingMethod="auto expand")';e+="";return e;},rad2deg:function(d){var e=d*(180/Math.PI);return e;},deg2rad:function(e){var d=e*(Math.PI/180);return d;},angle2rad:function(d){if(typeof d==="string"&&d.indexOf("rad")>-1){d=parseFloat(d);}else{d=this.deg2rad(parseFloat(d));}return d;},rotate:function(g,e,i){var d=this.angle2rad(g),f=Math.sin(d),h=Math.cos(d);this.multiply(h,f,0-f,h,0,0);return this;},translate:function(d,e){d=parseFloat(d)||0;e=parseFloat(e)||0;this.multiply(1,0,0,1,d,e);return this;},translateX:function(d){this.translate(d);return this;},translateY:function(d){this.translate(null,d);return this;},identity:function(){var d=this._config,e=this._defaults,f;for(f in d){if(f in e){this[f]=e[f];}}return this;},getMatrixArray:function(){var d=this,e=[[d.a,d.c,d.dx],[d.b,d.d,d.dy],[0,0,1]];return e;},getContentRect:function(q,p,l,k){var i=!isNaN(l)?l:0,o=!isNaN(k)?k:0,z=i+q,j=o+p,s=this,C=s.a,B=s.b,A=s.c,w=s.d,n=s.dx,m=s.dy,v=(C*i+A*o+n),h=(B*i+w*o+m),u=(C*z+A*o+n),g=(B*z+w*o+m),t=(C*i+A*j+n),f=(B*i+w*j+m),r=(C*z+A*j+n),e=(B*z+w*j+m);return{left:Math.min(t,Math.min(v,Math.min(u,r))),right:Math.max(t,Math.max(v,Math.max(u,r))),top:Math.min(g,Math.min(e,Math.min(f,h))),bottom:Math.max(g,Math.max(e,Math.max(f,h)))};},getDeterminant:function(){return c.MatrixUtil.getDeterminant(this.getMatrixArray());
},inverse:function(){return c.MatrixUtil.inverse(this.getMatrixArray());},transpose:function(){return c.MatrixUtil.transpose(this.getMatrixArray());},decompose:function(){return c.MatrixUtil.decompose(this.getMatrixArray());}};c.Matrix=b;},"3.6.0",{requires:["yui-base"]});