import { AtButton, AtDivider, AtRadio } from "taro-ui";
import { useEffect, useState } from "react";
import { View } from "@tarojs/components";
import "./index.scss";
import GlobalFooter from "../../components/GlobalFooter";
import questions from "../../data/questions.json";
import Taro from "@tarojs/taro";

export default () => {
  // 当前题目对象
  const [question, setQuestion] = useState(questions[0]);
  // 当前题目选项集合
  const questionOptions = question.options.map((item) => {
    return { label: `${item.key}.${item.value}`, value: item.key };
  });
  // 当前题号
  const [currentNum, setCurrentNum] = useState(1);

  // 当前回答
  const [currentAnswer, setCurrentAnswer] = useState<string>();
  // 回答列表
  const [answerList] = useState<string[]>([]);
  // 题号修改的同时修改题目
  useEffect(() => {
    setQuestion(questions[currentNum - 1]);
    setCurrentAnswer(answerList[currentNum - 1]);
  }, [answerList, currentNum]);
  return (
    <View className="doQuestion">
      {/*要用这种方式查看数组元素*/}
      {/*{JSON.stringify(answerList)}*/}
      {/*题目*/}
      <View className="at-article__h1 quesitonTitle">
        {currentNum}.{question.title}
      </View>
      {/*选项框*/}
      <AtRadio
        options={questionOptions}
        value={currentAnswer}
        onClick={(value) => {
          console.log(value);
          setCurrentAnswer(value);
          // 记录回答
          answerList[currentNum - 1] = value;
        }}
      />
      {answerList}
      <AtDivider />
      {currentNum === questions.length && (
        <AtButton
          type="primary"
          circle
          className="controlBtn"
          onClick={() => {
            // 设置结果数据
            Taro.setStorageSync("answerList", answerList);
            Taro.navigateTo({
              url: "/pages/result/index",
            });
          }}
        >
          查看结果
        </AtButton>
      )}
      {currentNum < questions.length && (
        <AtButton
          type="primary"
          circle
          className="controlBtn"
          onClick={() => setCurrentNum(currentNum + 1)}
        >
          下一题
        </AtButton>
      )}
      {currentNum > 1 && (
        <AtButton
          circle
          className="controlBtn"
          onClick={() => setCurrentNum(currentNum - 1)}
        >
          上一题
        </AtButton>
      )}
      <GlobalFooter />
    </View>
  );
};
