(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([[7],{bzVu:function(e,t,r){"use strict";var u=r("g09b");Object.defineProperty(t,"__esModule",{value:!0}),t.default=void 0;var n=u(r("d6i3")),a=r("xPJJ"),o={namespace:"group",state:{},effects:{postGroup:n.default.mark(function e(t,r){var u,o,p;return n.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return u=t.payload,o=r.call,e.next=4,o(a.postGroup,u);case 4:return p=e.sent,e.abrupt("return",p);case 6:case"end":return e.stop()}},e)}),postGroupMore:n.default.mark(function e(t,r){var u,o,p;return n.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return u=t.payload,o=r.call,e.next=4,o(a.postGroupMore,u);case 4:return p=e.sent,e.abrupt("return",p);case 6:case"end":return e.stop()}},e)}),uploadExcel:n.default.mark(function e(t,r){var u,o,p;return n.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return u=t.payload,o=r.call,e.next=4,o(a.uploadExcel,u);case 4:return p=e.sent,e.abrupt("return",p);case 6:case"end":return e.stop()}},e)})},reducers:{}};t.default=o},xPJJ:function(e,t,r){"use strict";var u=r("g09b");Object.defineProperty(t,"__esModule",{value:!0}),t.postGroup=p,t.postGroupMore=s,t.uploadExcel=i;var n=u(r("d6i3")),a=u(r("1l/V")),o=u(r("t3Un"));function p(e){return c.apply(this,arguments)}function c(){return c=(0,a.default)(n.default.mark(function e(t){return n.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return e.abrupt("return",(0,o.default)("/group/groupForDevice",{method:"POST",body:t}));case 1:case"end":return e.stop()}},e)})),c.apply(this,arguments)}function s(e){return l.apply(this,arguments)}function l(){return l=(0,a.default)(n.default.mark(function e(t){return n.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return e.abrupt("return",(0,o.default)("/group/groupForDeviceStars",{method:"POST",body:t}));case 1:case"end":return e.stop()}},e)})),l.apply(this,arguments)}function i(e){return d.apply(this,arguments)}function d(){return d=(0,a.default)(n.default.mark(function e(t){return n.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return e.abrupt("return",(0,o.default)("/group/groupWithExcel",{method:"POST",body:t,isNotFormData:1}));case 1:case"end":return e.stop()}},e)})),d.apply(this,arguments)}}}]);