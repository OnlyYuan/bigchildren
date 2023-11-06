package com.cds.bigchildren.common.route


//接收信息

/**
 * MainActivity
 */

//1.点击故事
val mainTouchStoryBtn = "MainActivity/goStory"
//2.点击读故事
val mainTouchReadBtn = "MainActivity/goRead"
//3.点击答题
val mainTouchAnswerBtn = "MainActivity/goAnswer"
//4.点击游戏
val mainTouchGameBtn = "MainActivity/goGame"
//5点击总结
val mainToucSummaryBtn = "MainActivity/goSummary"
//6.点击下一个标签按钮
val mainTouchNextBtn = "MainActivity/goNext"
//7.点击上一个标签按钮
val mainTouchBackBtn = "MainActivity/goBack"

/**
 * StoryActivity
 */
//1.播放
val storyPlayBtn = "StoryActivity/play"

//2.返回
val storyBackBtn = "StoryActivity/goBack"

//3.进入下一项（跟读）
val storyNextBtn = "StoryActivity/goNext"

/**
 * ReadActivity
 */
//1.开始跟读
val readStartReadBtn = "ReadActivity/startRead"

//2.返回按钮
val readBackeBtn = "ReadActivity/goBack"

//3.播放下一段
val readNextVoiceBtn = "ReadActivity/nextVoice"

//4.进入到答题界面
val readGoAnswerBtn = "ReadActivity/goAnswer"

//5.得分
val readScore = "ReadActivity/score"

//6.录制完成
val readRecordOkScore = "ReadActivity/recordOk"


/**
 * QuestionActivity
 */

//1.选择答案
val questionSelectAnswer = "QuestionActivity/selectAnswer"

//2.返回
val questiongoBack = "QuestionActivity/goBack"

/**
 * SummaryActivity
 */
//1.进入下一章
val summaryGoNext = "SummaryActivity/goNext"

//2.关闭
val summaryClose = "SummaryActivity/close"