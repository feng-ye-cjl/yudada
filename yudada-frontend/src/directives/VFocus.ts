// 在模板中启用 v-focus
const vFocus = {
  mounted: (el: any) => {
    el.getElementsByTagName("input")[0].focus();
  },
};

export default vFocus;
