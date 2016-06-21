#include "../../include/core/chars_identify.h"
#include "../../include/config.h"
#include "../../include/core/core_func.h"

namespace easypr {

CharsIdentify* CharsIdentify::instance_ = nullptr;

CharsIdentify* CharsIdentify::instance() {
  if (!instance_) {
    instance_ = new CharsIdentify;
  }
  return instance_;
}

CharsIdentify::CharsIdentify() {
  kv_ = std::shared_ptr<Kv>(new Kv);
  kv_->add("zh_cuan", "川");
  kv_->add("zh_gan1", "甘");
  kv_->add("zh_hei", "黑");
  kv_->add("zh_jin", "津");
  kv_->add("zh_liao", "辽");
  kv_->add("zh_min", "闽");
  kv_->add("zh_qiong", "琼");
  kv_->add("zh_sx", "晋");
  kv_->add("zh_xin", "新");
  kv_->add("zh_yue", "粤");
  kv_->add("zh_zhe", "浙");
  kv_->add("zh_e", "鄂");
  kv_->add("zh_gui", "贵");
  kv_->add("zh_hu", "沪");
  kv_->add("zh_jing", "京");
  kv_->add("zh_lu", "鲁");
  kv_->add("zh_ning", "宁");
  kv_->add("zh_shan", "陕");
  kv_->add("zh_wan", "皖");
  kv_->add("zh_yu", "豫");
  kv_->add("zh_yun", "云");
  kv_->add("zh_gan", "赣");
  kv_->add("zh_gui1", "桂");
  kv_->add("zh_ji", "冀");
  kv_->add("zh_jl", "吉");
  kv_->add("zh_meng", "蒙");
  kv_->add("zh_qing", "青");
  kv_->add("zh_su", "苏");
  kv_->add("zh_xiang", "湘");
  kv_->add("zh_yu1", "渝");
  kv_->add("zh_zang", "藏");
}

void CharsIdentify::loadANN(const char* s) {
    if (NULL == s) {
        ann_ = ml::ANN_MLP::load<ml::ANN_MLP>(kDefaultAnnPath);
    } else {
        ann_ = ml::ANN_MLP::load<ml::ANN_MLP>(s);
    }
}

std::pair<std::string, std::string> CharsIdentify::identify(cv::Mat input) {
  cv::Mat feature = features(input, kPredictSize);
  auto index = static_cast<int>(ann_->predict(feature));
  if (index < 34) {
    return std::make_pair(kChars[index], kChars[index]);
  } else {
    const char* key = kChars[index];
    std::string s = key;
    std::string province = kv_->get(s);
    return std::make_pair(s, province);
  }
}
}
