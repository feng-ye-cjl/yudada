import 'taro-ui/dist/style/index.scss' // 全局引入一次即可
import { Component, PropsWithChildren } from 'react'
import './app.scss'
// 引入taro-ui组件库

class App extends Component<PropsWithChildren>  {

  componentDidMount () {}

  componentDidShow () {}

  componentDidHide () {}

  // this.props.children 是将要会渲染的页面
  render () {
    return this.props.children
  }
}

export default App
