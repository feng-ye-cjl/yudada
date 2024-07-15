import { AtButton } from "taro-ui";
import { Image, View } from "@tarojs/components";
import Taro from "@tarojs/taro";
import "./index.scss";
import backgroundImg from "../../assets/bg.jpg";
import GlobalFooter from "../../components/GlobalFooter";

export default () => {
  /**
   * 跳转到做题页面
   */
  const toDoQuestion = () => {
    Taro.navigateTo({
      url: "/pages/doQuestion/index",
    });
  };
  return (
    <View className="indexPage">
      <View className="at-article__h1 title">MBTI 性格测试</View>
      <View className="at-article__h3 subtitle">
        只需2分钟，即可准确的描述出你是谁，以及你的性格特点
      </View>
      <AtButton
        type="primary"
        circle
        className="enterBtn"
        onClick={toDoQuestion}
      >
        开始测试
      </AtButton>
      <Image src={backgroundImg} style={{ width: "100%", height: "100%" }} mode="aspectFill" />
      <GlobalFooter />
    </View>
  );
};
