(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([[6],{BuOD:function(e,t,a){"use strict";var r=a("g09b");Object.defineProperty(t,"__esModule",{value:!0}),t.getCompanyList=o,t.addFactoryName=s,t.postFactoryName=i,t.delFactoryName=f;var n=r(a("d6i3")),u=r(a("1l/V")),c=r(a("t3Un"));function o(e){return p.apply(this,arguments)}function p(){return p=(0,u.default)(n.default.mark(function e(t){return n.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return e.abrupt("return",(0,c.default)("/factory/query",{method:"POST",body:t}));case 1:case"end":return e.stop()}},e)})),p.apply(this,arguments)}function s(e){return d.apply(this,arguments)}function d(){return d=(0,u.default)(n.default.mark(function e(t){return n.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return e.abrupt("return",(0,c.default)("/factory/addFactory",{method:"POST",body:t}));case 1:case"end":return e.stop()}},e)})),d.apply(this,arguments)}function i(e){return l.apply(this,arguments)}function l(){return l=(0,u.default)(n.default.mark(function e(t){return n.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return e.abrupt("return",(0,c.default)("/factory/edit",{method:"POST",body:t}));case 1:case"end":return e.stop()}},e)})),l.apply(this,arguments)}function f(e){return y.apply(this,arguments)}function y(){return y=(0,u.default)(n.default.mark(function e(t){return n.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return e.abrupt("return",(0,c.default)("/factory/deleteFactory",{method:"POST",body:t}));case 1:case"end":return e.stop()}},e)})),y.apply(this,arguments)}},xGm7:function(e,t,a){"use strict";var r=a("g09b");Object.defineProperty(t,"__esModule",{value:!0}),t.default=void 0;var n=r(a("p0pE")),u=r(a("d6i3")),c=a("BuOD"),o={namespace:"company",state:{companyList:[],companyPage:{current:1,total:0,pageSize:1}},effects:{getCompanyList:u.default.mark(function e(t,a){var r,n,o,p;return u.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return r=t.payload,n=a.call,o=a.put,e.next=4,n(c.getCompanyList,r);case 4:if(p=e.sent,!p.code){e.next=8;break}return e.next=8,o({type:"saveCompanyList",payload:p.data});case 8:case"end":return e.stop()}},e)}),addFactoryName:u.default.mark(function e(t,a){var r,n,o,p;return u.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return r=t.payload,n=a.call,o=a.put,e.next=4,n(c.addFactoryName,r);case 4:if(p=e.sent,!p.code){e.next=8;break}return e.next=8,o({type:"device/getSelectCompanyList"});case 8:return e.abrupt("return",p);case 9:case"end":return e.stop()}},e)}),postFactoryName:u.default.mark(function e(t,a){var r,n,o,p;return u.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return r=t.payload,n=a.call,o=a.put,e.next=4,n(c.postFactoryName,r);case 4:if(p=e.sent,!p.code){e.next=10;break}return e.next=8,o({type:"device/getSelectCompanyList"});case 8:return e.next=10,o({type:"device/saveList",payload:[]});case 10:return e.abrupt("return",p);case 11:case"end":return e.stop()}},e)}),delFactoryName:u.default.mark(function e(t,a){var r,n,o,p;return u.default.wrap(function(e){while(1)switch(e.prev=e.next){case 0:return r=t.payload,n=a.call,o=a.put,e.next=4,n(c.delFactoryName,r);case 4:if(p=e.sent,!p.code){e.next=8;break}return e.next=8,o({type:"device/getSelectCompanyList"});case 8:return e.abrupt("return",p);case 9:case"end":return e.stop()}},e)})},reducers:{saveCompanyList:function(e,t){var a=t.payload;return(0,n.default)({},e,{companyList:a.datas,companyPage:{current:a.page,total:a.totalRecord,pageSize:a.pageSize}})}}};t.default=o}}]);