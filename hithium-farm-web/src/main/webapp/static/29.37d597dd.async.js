(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([[29],{dMF0:function(e,t,a){"use strict";var l=a("g09b"),r=a("tAuX");Object.defineProperty(t,"__esModule",{value:!0}),t.default=void 0,a("IzEo");var n=l(a("bx4M"));a("2qtc");var d=l(a("kLXV")),u=l(a("jehZ"));a("g9YV");var o=l(a("wCAj"));a("Pwec");var f=l(a("CtXQ"));a("DZo9");var c=l(a("8z0m"));a("iQDF");var i=l(a("+eQT"));a("+L6B");var s=l(a("2/Rp"));a("5NDa");var m=l(a("5rEg"));a("14J3");var p=l(a("BMrR"));a("jCWc");var E=l(a("kPKH"));a("5Dmo");var h=l(a("3S7+"));a("+BJd");var v=l(a("mr32"));a("miYZ");var g=l(a("tsqr")),y=l(a("p0pE")),x=l(a("2Taf")),b=l(a("vZ4D")),w=l(a("l4Ni")),S=l(a("ujKo")),D=l(a("MhPg"));a("OaEy");var O=l(a("2fM7"));a("y8nQ");var T,N,C,M,I=l(a("Vl3Y")),k=r(a("q1tI")),Y=a("MuoO"),L=l(a("wd/R")),R=l(a("EUZL")),j=l(a("xNuS")),B=l(a("zHco")),V=a("+n12"),z=I.default.Item,F=O.default.Option,H=(T=I.default.create(),N=(0,Y.connect)(function(e){var t=e.alermLog,a=e.loading;return{alermLog:t,tableLoading:a.effects["alermLog/getTableData"]}}),T(C=N((M=function(e){function t(){var e,a;(0,x.default)(this,t);for(var l=arguments.length,r=new Array(l),n=0;n<l;n++)r[n]=arguments[n];return a=(0,w.default)(this,(e=(0,S.default)(t)).call.apply(e,[this].concat(r))),a.state={searchData:{page:1,pageSize:10},showMore:!1,isExport:0,excelVisible:!1,isExportExcel:!1},a.getList=function(){var e=a.props.dispatch,t=a.state.searchData;e({type:"alermLog/getTableData",payload:(0,y.default)({},t)}).then(function(){a.setState({isExport:0})})},a.showTotal=function(e,t){return k.default.createElement("span",null,"\u663e\u793a\u7b2c".concat(t[0],"\u5230\u7b2c ").concat(t[1]," \u6761\u8bb0\u5f55\uff0c\u603b\u5171 ").concat(e," \u6761\u8bb0\u5f55"))},a.handleReset=function(e){e.preventDefault();var t=a.props.form;t.resetFields(),a.handleSubmit(e)},a.handleTableChange=function(e,t,l){var r=a.state.searchData,n=l.order&&l.columnKey||"",d=l.order?"descend"===l.order?-1:1:"";a.setState({searchData:(0,y.default)({},r,{page:e.current,pageSize:e.pageSize,orderField:n,order:d})},function(){a.getList()})},a.importExcel=function(e){var t=a.props.dispatch,l=a.state.searchData;t({type:"alermLog/importExcel",payload:{rdbsns:e,fromTime:l.fromTime,toTime:l.toTime}}).then(function(e){1===e.code&&a.setState({isExport:1})})},a.handleExport=function(e){e.preventDefault();var t=a.props,l=t.form,r=t.dispatch,n=a.state.searchData;l.validateFields(["fileName"],function(e,t){if(!e){var l=(0,y.default)({},t,n);delete l.page,delete l.pageSize,a.setState({isExportExcel:!0}),r({type:"alermLog/exportData",payload:(0,y.default)({},l)}).then(function(e){e.success?(g.default.loading("\u6587\u4ef6\u5bfc\u51fa\u4e2d\uff0c\u8bf7\u8010\u5fc3\u7b49\u5f85\uff01",0),a.timer=setInterval(function(){r({type:"common/getExportResult",payload:{recordId:e.data.id}}).then(function(e){e.success?e.data.filename&&(clearInterval(a.timer),g.default.destroy(),a.setState({excelVisible:!1,isExportExcel:!1},function(){var t={fileName:e.data.filename,originName:e.data.originName,token:localStorage.getItem("huihan-token")};document.getElementById("tempiframe").src="/excel/download?".concat((0,V.jsonToQueryString)(t))})):(a.setState({isExportExcel:!1}),clearInterval(a.timer),g.default.destroy())})},2e3)):a.setState({isExportExcel:!1})})}})},a.handleSubmit=function(e){e.preventDefault();var t=a.props.form,l=a.state.searchData;t.validateFields(function(e,t){if(!e){var r=(0,y.default)({},l,t,{page:1,fromTime:t.fromTime&&t.fromTime.format("YYYY-MM-DD HH:mm:ss"),toTime:t.toTime&&t.toTime.format("YYYY-MM-DD HH:mm:ss")});delete r.fileName,a.setState({searchData:r},function(){a.getList()})}})},a.disabledStartDate=function(e){var t=a.props.form.getFieldValue,l=t("toTime");return l?(0,L.default)(e).valueOf()<(0,L.default)().subtract(6,"day").startOf("day").valueOf()||(0,L.default)(e).valueOf()>(0,L.default)(l).valueOf():(0,L.default)(e).valueOf()<(0,L.default)().subtract(6,"day").startOf("day").valueOf()||(0,L.default)(e).valueOf()>(0,L.default)().endOf("day").valueOf()},a.disabledEndDate=function(e){var t=a.props.form.getFieldValue,l=t("fromTime");return l?(0,L.default)(e).valueOf()>(0,L.default)().endOf("day").valueOf()||(0,L.default)(e).valueOf()<(0,L.default)(l).valueOf():(0,L.default)(e).valueOf()<(0,L.default)().subtract(6,"day").startOf("day").valueOf()||(0,L.default)(e).valueOf()>(0,L.default)().endOf("day").valueOf()},a}return(0,D.default)(t,e),(0,b.default)(t,[{key:"componentDidMount",value:function(){this.getList()}},{key:"componentWillUnmount",value:function(){clearInterval(this.timer),g.default.destroy()}},{key:"render",value:function(){var e=this,t=this.props,a=t.form.getFieldDecorator,l=t.alermLog,r=t.tableLoading,x=l.tableList,b=l.tablePage,w=this.state,S=w.showMore,D=w.isExport,T=w.excelVisible,N=w.isExportExcel,C={labelCol:{span:6},wrapperCol:{span:14}},M=[{title:"\u5e8f\u53f7",dataIndex:"id",width:100,render:function(e,t,a){return(b.current-1)*b.pageSize+a+1}},{title:"RDB-SN",width:200,dataIndex:"rdbsn",render:function(e){return k.default.createElement(j.default,{tooltip:!0,lines:1},e)}},{title:"CAN0\u94fe\u8def\u72b6\u6001",width:200,dataIndex:"can0State",render:function(e){return e?k.default.createElement(v.default,{color:"red"},1===e?"busOff":2===e?"\u521d\u59cb\u5316\u5931\u8d25":"busOff,\u521d\u59cb\u5316\u5931\u8d25"):k.default.createElement(v.default,{color:"green"},"\u6b63\u5e38")}},{title:"\u6982\u8981\u6570\u636e\u91c7\u96c6\u5b8c\u6574",width:200,dataIndex:"generalCollectComplete",render:function(e){return e?k.default.createElement(v.default,{color:"green"},"\u91c7\u96c6\u5b8c\u6574"):k.default.createElement(v.default,{color:"red"},"\u91c7\u96c6\u4e0d\u5b8c\u6574")}},{title:"BMU\u5728\u7ebf\u72b6\u6001",width:200,dataIndex:"bmuOnline",render:function(e){return e?k.default.createElement(v.default,{color:"green"},"\u5728\u7ebf"):k.default.createElement(v.default,null,"\u79bb\u7ebf")}},{title:"\u4e0a\u62a5\u65f6\u95f4",width:200,dataIndex:"recordTime",sorter:!D||function(e,t){return e.recordTime.localeCompare(t.recordTime)},render:function(e){return e||"--"}},{title:"GSM",width:150,dataIndex:"gsmState",render:function(e,t){return e?k.default.createElement(h.default,{title:t.gsmErrors&&t.gsmErrors.join(",")},k.default.createElement(v.default,{color:"red"},"\u5f02\u5e38:",t.gsmCode)):k.default.createElement(v.default,{color:"green"},"\u6b63\u5e38")}},{title:"\u5b58\u50a8\u72b6\u6001",width:150,dataIndex:"flushState",render:function(e,t){return e?k.default.createElement(h.default,{title:t.flushErrors&&t.flushErrors.join(",")},k.default.createElement(v.default,{color:"red"},"\u5f02\u5e38:",t.flushCode)):k.default.createElement(v.default,{color:"green"},"\u6b63\u5e38")}},{title:"\u8fdc\u7a0b\u5e94\u7528",width:150,dataIndex:"tspState",render:function(e,t){return e?k.default.createElement(h.default,{title:t.tspErrors&&t.tspErrors.join(",")},k.default.createElement(v.default,{color:"red"},"\u5f02\u5e38:",t.tspCode)):k.default.createElement(v.default,{color:"green"},"\u6b63\u5e38")}},{title:"EEPROM",width:150,dataIndex:"eepromState",render:function(e,t){return e?k.default.createElement(h.default,{title:t.eepromErrors&&t.eepromErrors.join(",")},k.default.createElement(v.default,{color:"red"},"\u5f02\u5e38:",t.eepromCode)):k.default.createElement(v.default,{color:"green"},"\u6b63\u5e38")}}],Y={accept:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel",name:"file",showUploadList:!1,beforeUpload:function(t,a){var l="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"===t.type||"application/vnd.ms-excel";if(!l)return g.default.error("\u6587\u4ef6\u683c\u5f0f\u9519\u8bef\uff0c\u5e94\u4e3aExcel\u6587\u4ef6!"),!1;var r=!0,n=a[0],d=new FileReader;return d.onload=function(t){var a=t.target.result;r||(a=new Uint8Array(a));var l=R.default.read(a,{type:r?"binary":"array"}),n=l.Sheets[l.SheetNames[0]],d=R.default.utils.sheet_to_json(n,{header:1});if(1===d[0].length&&"RDBSN"===d[0][0]){var u=[];d.slice(1).forEach(function(e){e[0]&&u.push(e[0])}),u.length?u.length>10?g.default.error("Excel\u5bfc\u5165\u68c0\u7d22\u7684\u8bbe\u5907\u6761\u76ee\u4e0d\u5f97\u8d85\u8fc710\u6761"):e.importExcel(u.join(",")):g.default.error("excel \u65e0\u6570\u636e")}else g.default.error("excel \u683c\u5f0f\u9519\u8bef")},r?d.readAsBinaryString(n):d.readAsArrayBuffer(n),!1}},V=k.default.createElement("div",null,k.default.createElement("span",null,"Excel\u5bfc\u5165\u68c0\u7d22\u7684\u8bbe\u5907\u6761\u76ee\u4e0d\u5f97\u8d85\u8fc710\u6761\uff0c\u683c\u5f0f\u4e3a\uff1a"),k.default.createElement(p.default,null,k.default.createElement(E.default,{span:6},"RDBSN")),k.default.createElement(p.default,{className:"mb-10"},k.default.createElement(E.default,{span:6},"rdbsn1")),k.default.createElement("a",{href:"/static/rdbsn.xlsx",style:{display:"inlineBlock",padding:"5px 10px",backgroundColor:"#3768E9",borderRadius:5},className:"white",target:"tempiframe"},"\u4e0b\u8f7d\u6a21\u677f"));return k.default.createElement(B.default,null,k.default.createElement(n.default,{bordered:!1,bodyStyle:{padding:0}},k.default.createElement(I.default,{layout:"inline",hideRequiredMark:!0,onSubmit:this.handleSubmit},k.default.createElement(z,null,a("rdbsn")(k.default.createElement(m.default,{className:"wd-100",placeholder:"RDB SN"}))),k.default.createElement(z,null,a("canError")(k.default.createElement(O.default,{className:"wd-100",placeholder:"CAN0\u94fe\u8def"},k.default.createElement(F,{value:"0"},"\u6b63\u5e38"),k.default.createElement(F,{value:"1"},"busOff"),k.default.createElement(F,{value:"2"},"\u521d\u59cb\u5316\u5931\u8d25")))),k.default.createElement(z,null,a("gsmError")(k.default.createElement(O.default,{className:"wd-100",placeholder:"GSM"},k.default.createElement(F,{value:"false"},"\u6b63\u5e38"),k.default.createElement(F,{value:"true"},"\u5f02\u5e38")))),k.default.createElement(z,null,k.default.createElement(s.default,{type:"primary",htmlType:"submit"},"\u641c\u7d22")),k.default.createElement(z,null,k.default.createElement(s.default,{onClick:this.handleReset},"\u91cd\u7f6e")),k.default.createElement(z,null,k.default.createElement(s.default,{onClick:function(){e.setState({showMore:!S})}},S?"\u9690\u85cf":"\u66f4\u591a")),k.default.createElement("br",null),S?k.default.createElement("span",null,k.default.createElement(z,null,a("flushError")(k.default.createElement(O.default,{className:"wd-100",placeholder:"\u5b58\u50a8\u72b6\u6001"},k.default.createElement(F,{value:"true"},"\u5f02\u5e38"),k.default.createElement(F,{value:"false"},"\u6b63\u5e38")))),k.default.createElement(z,null,a("tspError")(k.default.createElement(O.default,{className:"wd-100",placeholder:"\u8fdc\u7a0b\u5e94\u7528"},k.default.createElement(F,{value:"false"},"\u6b63\u5e38"),k.default.createElement(F,{value:"true"},"\u5f02\u5e38")))),k.default.createElement(z,null,a("eepromError")(k.default.createElement(O.default,{className:"wd-100",placeholder:"EEPROM"},k.default.createElement(F,{value:"false"},"\u6b63\u5e38"),k.default.createElement(F,{value:"true"},"\u5f02\u5e38")))),k.default.createElement(z,null,a("bmuOnline")(k.default.createElement(O.default,{className:"wd-150",placeholder:"BMU\u5728\u7ebf\u72b6\u6001"},k.default.createElement(F,{value:"true"},"\u5728\u7ebf"),k.default.createElement(F,{value:"false"},"\u79bb\u7ebf")))),k.default.createElement(z,null,a("collectComplete")(k.default.createElement(O.default,{className:"wd-150",placeholder:"\u6982\u8981\u6570\u636e\u91c7\u96c6\u5b8c\u6574"},k.default.createElement(F,{value:"true"},"\u5b8c\u6574"),k.default.createElement(F,{value:"false"},"\u4e0d\u5b8c\u6574")))),k.default.createElement(z,null,a("fromTime",{rules:[{type:"object"}],initialValue:(0,L.default)((0,L.default)().subtract(6,"day").startOf("day"))})(k.default.createElement(i.default,{format:"YYYY-MM-DD HH:mm:ss",disabledDate:this.disabledStartDate,showTime:!0,placeholder:"\u4e0a\u62a5\u5f00\u59cb\u65f6\u95f4"}))),k.default.createElement("span",{className:"line"},"-"),k.default.createElement(z,null,a("toTime",{rules:[{type:"object"}],initialValue:(0,L.default)().endOf("day")})(k.default.createElement(i.default,{format:"YYYY-MM-DD HH:mm:ss",disabledDate:this.disabledEndDate,showTime:!0,placeholder:"\u4e0a\u62a5\u7ed3\u675f\u65f6\u95f4"}))),k.default.createElement("br",null)):"",k.default.createElement(z,null,k.default.createElement(s.default,{type:"primary",onClick:function(){e.setState({excelVisible:!0})}},"\u8bbe\u5907\u5bfc\u51fa")),k.default.createElement(z,null,k.default.createElement(c.default,Y,k.default.createElement(s.default,{type:"primary"},"\u5bfc\u5165\u68c0\u7d22")),k.default.createElement(h.default,{placement:"bottom",title:V},k.default.createElement(f.default,{className:"ml-10",style:{fontSize:16},type:"question-circle",theme:"filled"})))),D?k.default.createElement(o.default,{className:"mt-20",rowKey:function(e){return e.id},dataSource:x,pagination:{showSizeChanger:!0,showQuickJumper:!0,showTotal:this.showTotal},columns:M,scroll:{x:1750,y:500}}):k.default.createElement(o.default,{className:"mt-20",rowKey:function(e){return e.id},dataSource:x,pagination:(0,y.default)({},b,{showSizeChanger:!0,showQuickJumper:!0,showTotal:this.showTotal}),columns:M,onChange:this.handleTableChange,loading:r,scroll:{x:1750,y:500}}),k.default.createElement(d.default,{title:"Excel\u6587\u4ef6\u5bfc\u51fa",visible:T,onOk:x.length?this.handleExport:function(){e.setState({excelVisible:!1})},onCancel:function(){clearInterval(e.timer),g.default.destroy(),e.setState({excelVisible:!1,isExportExcel:!1})},maskClosable:!1,okButtonProps:{disabled:N},destroyOnClose:!0},x.length?k.default.createElement(I.default,null,k.default.createElement(z,(0,u.default)({},C,{label:"\u6587\u4ef6\u540d",extra:"\u8bf7\u524d\u5f80\u6d4f\u89c8\u5668\u4e0b\u8f7d\u5185\u5bb9\u9875\u67e5\u770bExcel\u5bfc\u51fa\u72b6\u6001"}),a("fileName",{rules:[{required:!0,message:"\u8bf7\u8f93\u5165\u6587\u4ef6\u540d"}],initialValue:"\u544a\u8b66\u65e5\u5fd7"})(k.default.createElement(m.default,{placeholder:"\u8bf7\u8f93\u5165\u6587\u4ef6\u540d"})))):k.default.createElement("div",null,"\u8be5\u6761\u4ef6\u4e0b\u4e0d\u5b58\u5728\u8bbe\u5907\uff0c\u8bf7\u91cd\u65b0\u7b5b\u9009\uff01"))))}}]),t}(k.PureComponent),C=M))||C)||C),A=H;t.default=A}}]);