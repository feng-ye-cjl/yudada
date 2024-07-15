import { AtButton } from "taro-ui";
import { Image, View } from "@tarojs/components";
import Taro from "@tarojs/taro";
import questionResult from "../../data/question_results.json";
import "./index.scss";
import backgroundImg from "../../assets/bg.jpg";
import GlobalFooter from "../../components/GlobalFooter";
import { getBestQuestionResult } from "../../utils/BizUtils";
import questions from "../../data/questions.json";

export default () => {
  // const result = questionResult[0];
  // 获取用户答案结果
  const answerList = Taro.getStorageSync("answerList");
  if (!answerList || answerList.length === 0) {
    Taro.showToast({
      title: '答案异常',
      icon: 'error',
      duration: 3000
    })
  }

  const result = getBestQuestionResult(answerList, questions, questionResult);

  const toIndex = () => {
    Taro.reLaunch({
      url: "/pages/index/index",
    });
  };
  return (
    <View className="result">
      <View className="at-article__h1 title">{result.resultName}</View>
      <View className="at-article__h3 subtitle">{result.resultDesc}</View>
      <AtButton type="primary" circle className="enterBtn" onClick={toIndex}>
        返回主页
      </AtButton>
      <Image src={backgroundImg} style={{ width: "100%",height: "100%" }} mode="aspectFill" />
      <GlobalFooter />
    </View>
  );
};
