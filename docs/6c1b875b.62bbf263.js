(window.webpackJsonp=window.webpackJsonp||[]).push([[41],{108:function(e,t,n){"use strict";n.r(t),n.d(t,"frontMatter",(function(){return c})),n.d(t,"metadata",(function(){return s})),n.d(t,"rightToc",(function(){return a})),n.d(t,"default",(function(){return p}));var r=n(3),o=n(7),i=(n(0),n(159)),c={id:"wiremock",title:"WireMock",sidebar_label:"WireMock",slug:"wiremock.html"},s={unversionedId:"extensions/wiremock",id:"extensions/wiremock",isDocsHomePage:!1,title:"WireMock",description:"WireMock",source:"@site/docs/extensions/wiremock.md",slug:"/extensions/wiremock.html",permalink:"/docs/extensions/wiremock.html",editUrl:"https://github.com/kotest/kotest/blob/master/documentation/docs/extensions/wiremock.md",version:"current",sidebar_label:"WireMock",sidebar:"extensions",previous:{title:"Koin",permalink:"/docs/extensions/koin.html"},next:{title:"Robolectric",permalink:"/docs/extensions/robolectric.html"}},a=[{value:"WireMock",id:"wiremock",children:[]}],l={rightToc:a};function p(e){var t=e.components,n=Object(o.a)(e,["components"]);return Object(i.b)("wrapper",Object(r.a)({},l,n,{components:t,mdxType:"MDXLayout"}),Object(i.b)("h2",{id:"wiremock"},"WireMock"),Object(i.b)("p",null,Object(i.b)("a",Object(r.a)({parentName:"p"},{href:"https://github.com/tomakehurst/wiremock"}),"WireMock")," is a library which provides HTTP response stubbing, matchable on\nURL, header and body content patterns etc."),Object(i.b)("p",null,"Kotest provides a module ",Object(i.b)("inlineCode",{parentName:"p"},"kotest-extensions-wiremock")," for integration with wiremock."),Object(i.b)("p",null,Object(i.b)("a",Object(r.a)({parentName:"p"},{href:"https://search.maven.org/artifact/io.kotest.extensions/kotest-extensions-wiremock"}),Object(i.b)("img",{src:"https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-wiremock.svg?label=latest%20release"})),"\n",Object(i.b)("a",Object(r.a)({parentName:"p"},{href:"https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-extensions-wiremock/"}),Object(i.b)("img",{src:"https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-extensions-wiremock.svg?label=latest%20snapshot"}))),Object(i.b)("p",null,"To begin, add the following dependency to your build:"),Object(i.b)("pre",null,Object(i.b)("code",Object(r.a)({parentName:"pre"},{}),"io.kotest.extensions:kotest-extensions-wiremock:{version}\n")),Object(i.b)("p",null,"Having this dependency in the classpath brings ",Object(i.b)("inlineCode",{parentName:"p"},"WireMockListener")," into scope.\n",Object(i.b)("inlineCode",{parentName:"p"},"WireMockListener")," manages  the lifecycle of a ",Object(i.b)("inlineCode",{parentName:"p"},"WireMockServer")," during your test."),Object(i.b)("p",null,"For example:"),Object(i.b)("pre",null,Object(i.b)("code",Object(r.a)({parentName:"pre"},{className:"language-kotlin"}),'\nclass SomeTest : FunSpec({\n  val customerServiceServer = WireMockServer(9000)\n  listener(WireMockListener(customerServiceServer, ListenerMode.PER_SPEC))\n\n  test("let me get customer information") {\n    customerServiceServer.stubFor(\n      WireMock.get(WireMock.urlEqualTo("/customers/123"))\n        .willReturn(WireMock.ok())\n    )\n\n    val connection = URL("http://localhost:9000/customers/123").openConnection() as HttpURLConnection\n    connection.responseCode shouldBe 200\n  }\n\n    //  ------------OTHER TEST BELOW ----------------\n})\n')),Object(i.b)("p",null,"In above example we created an instance of ",Object(i.b)("inlineCode",{parentName:"p"},"WireMockListener")," which starts a ",Object(i.b)("inlineCode",{parentName:"p"},"WireMockServer")," before running the tests\nin the spec and stops it after completing all the tests in the spec."),Object(i.b)("p",null,"You can use ",Object(i.b)("inlineCode",{parentName:"p"},"WireMockServer.perSpec(customerServiceServer)")," to achieve same result."),Object(i.b)("pre",null,Object(i.b)("code",Object(r.a)({parentName:"pre"},{className:"language-kotlin"}),'\nclass SomeTest : FunSpec({\n  val customerServiceServer = WireMockServer(9000)\n  listener(WireMockListener(customerServiceServer, ListenerMode.PER_TEST))\n\n  test("let me get customer information") {\n    customerServiceServer.stubFor(\n      WireMock.get(WireMock.urlEqualTo("/customers/123"))\n        .willReturn(WireMock.ok())\n    )\n\n    val connection = URL("http://localhost:9000/customers/123").openConnection() as HttpURLConnection\n    connection.responseCode shouldBe 200\n  }\n\n  //  ------------OTHER TEST BELOW ----------------\n})\n')),Object(i.b)("p",null,"In above example we created an instance of ",Object(i.b)("inlineCode",{parentName:"p"},"WireMockListener")," which starts a ",Object(i.b)("inlineCode",{parentName:"p"},"WireMockServer")," before running every test\nin the spec and stops it after completing every test in the spec.\nYou can use ",Object(i.b)("inlineCode",{parentName:"p"},"WireMockServer.perTest(customerServiceServer)")," to achieve same result."))}p.isMDXComponent=!0},159:function(e,t,n){"use strict";n.d(t,"a",(function(){return u})),n.d(t,"b",(function(){return d}));var r=n(0),o=n.n(r);function i(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function c(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function s(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?c(Object(n),!0).forEach((function(t){i(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):c(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function a(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var l=o.a.createContext({}),p=function(e){var t=o.a.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):s(s({},t),e)),n},u=function(e){var t=p(e.components);return o.a.createElement(l.Provider,{value:t},e.children)},b={inlineCode:"code",wrapper:function(e){var t=e.children;return o.a.createElement(o.a.Fragment,{},t)}},m=o.a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,i=e.originalType,c=e.parentName,l=a(e,["components","mdxType","originalType","parentName"]),u=p(n),m=r,d=u["".concat(c,".").concat(m)]||u[m]||b[m]||i;return n?o.a.createElement(d,s(s({ref:t},l),{},{components:n})):o.a.createElement(d,s({ref:t},l))}));function d(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=n.length,c=new Array(i);c[0]=m;var s={};for(var a in t)hasOwnProperty.call(t,a)&&(s[a]=t[a]);s.originalType=e,s.mdxType="string"==typeof e?e:r,c[1]=s;for(var l=2;l<i;l++)c[l]=n[l];return o.a.createElement.apply(null,c)}return o.a.createElement.apply(null,n)}m.displayName="MDXCreateElement"}}]);