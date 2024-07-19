export default [
  {path: '/', name: '首页', icon: 'smile', component: './Index'},
  {path: '/interfaceInfo/:id', name: '接口详情', component: './InterfaceInfo', hideInMenu: true},
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      {name: '接口管理', path: '/admin/interfaceinfo/manage', component: './Admin/InterfaceInfo'},
      {name: '接口分析', path: '/admin/analysis', component: './Admin/Analysis'},
    ],
  },
  {
    path: '/user',
    layout: false,
    routes: [
      {name: '登录', path: '/user/login', component: './User/Login'},
    ],
  },
  {path: '*', layout: false, component: './404'},
];
