(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([[24],{v8K5:function(e,t,a){"use strict";var n=a("g09b"),l=a("tAuX");Object.defineProperty(t,"__esModule",{value:!0}),t.default=void 0,a("IzEo");var r=n(a("bx4M"));a("g9YV");var u=n(a("wCAj"));a("+L6B");var d=n(a("2/Rp"));a("5NDa");var o=n(a("5rEg")),i=n(a("p0pE")),c=n(a("2Taf")),s=n(a("vZ4D")),f=n(a("l4Ni")),h=n(a("ujKo")),p=n(a("MhPg"));a("y8nQ");var m,g,v,b,w=n(a("Vl3Y")),D=l(a("q1tI")),y=a("MuoO"),E=n(a("wY1l")),S=n(a("zHco")),L=w.default.Item,N=(m=w.default.create(),g=(0,y.connect)(function(e){var t=e.home,a=e.loading;return{home:t,tableLoading:a.effects["home/getDoneList"]}}),m(v=g((b=function(e){function t(){var e,a;(0,c.default)(this,t);for(var n=arguments.length,l=new Array(n),r=0;r<n;r++)l[r]=arguments[r];return a=(0,f.default)(this,(e=(0,h.default)(t)).call.apply(e,[this].concat(l))),a.state={searchData:{page:1,pageSize:10}},a.getList=function(){var e=a.props.dispatch,t=a.state.searchData;e({type:"home/getDoneList",payload:(0,i.default)({},t)})},a.showTotal=function(e,t){return D.default.createElement("span",null,"\u663e\u793a\u7b2c".concat(t[0],"\u5230\u7b2c ").concat(t[1]," \u6761\u8bb0\u5f55\uff0c\u603b\u5171 ").concat(e," \u6761\u8bb0\u5f55"))},a.handleReset=function(e){e.preventDefault();var t=a.props.form;t.resetFields(),a.handleSubmit(e)},a.handleTableChange=function(e){var t=a.state.searchData;a.setState({searchData:(0,i.default)({},t,{page:e.current,pageSize:e.pageSize})},function(){a.getList()})},a.handleSubmit=function(e){e.preventDefault();var t=a.props.form,n=a.state.searchData;t.validateFields(function(e,t){if(!e){var l=(0,i.default)({},n,{page:1,rdbsn:t.rdbsn});a.setState({searchData:l},function(){a.getList()})}})},a}return(0,p.default)(t,e),(0,s.default)(t,[{key:"componentDidMount",value:function(){this.getList()}},{key:"render",value:function(){var e=this.props,t=e.form.getFieldDecorator,a=e.tableLoading,n=e.home,l=n.doneList,c=n.donePage,s=[{title:"\u5e8f\u53f7",dataIndex:"id",width:100,render:function(e,t,a){return(c.current-1)*c.pageSize+a+1}},{title:"RDB-SN",dataIndex:"rdbsn"},{title:"\u4f20\u8f93\u6b21\u6570",dataIndex:"successRecord"},{title:"\u64cd\u4f5c",render:function(e,t){return D.default.createElement(E.default,{to:"/home/data/done/info?deviceName=".concat(t.deviceName)},"\u8be6\u60c5")}}];return D.default.createElement(S.default,null,D.default.createElement(r.default,{bordered:!1,bodyStyle:{padding:0}},D.default.createElement(w.default,{layout:"inline",hideRequiredMark:!0,onSubmit:this.handleSubmit},D.default.createElement(L,null,t("rdbsn")(D.default.createElement(o.default,{className:"wd-100",placeholder:"RDB SN"}))),D.default.createElement(L,null,D.default.createElement(d.default,{type:"primary",htmlType:"submit"},"\u641c\u7d22")),D.default.createElement(L,null,D.default.createElement(d.default,{onClick:this.handleReset},"\u91cd\u7f6e"))),D.default.createElement(u.default,{className:"mt-20",rowKey:function(e){return e.id},dataSource:l,pagination:(0,i.default)({},c,{showSizeChanger:!0,showQuickJumper:!0,showTotal:this.showTotal}),columns:s,onChange:this.handleTableChange,loading:a})))}}]),t}(D.PureComponent),v=b))||v)||v),k=N;t.default=k}}]);