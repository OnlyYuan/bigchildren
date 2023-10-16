package com.cds.bigchildren.util.di


import com.cds.bigchildren.model.datasource.GetConfigDatasource
import com.cds.bigchildren.model.datasource.NextQuestionDatasource
import com.cds.bigchildren.model.datasource.StartQuestionDatasource
import com.cds.bigchildren.model.repository.DataRepository
import com.cds.bigchildren.model.repository.QuestionRepository
import com.cds.bigchildren.viewmodel.MainViewModel
import com.cds.bigchildren.viewmodel.QuestionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * File Name: KoinModules.kt
 * Description: koin 依赖注入的 module
 * Author: cpf
 */
val appModule = module {
    //DataSource

    factory {
        GetConfigDatasource()
    }
    factory {
        StartQuestionDatasource()
    }
    factory {
        NextQuestionDatasource()
    }



    factory {
        DataRepository(get())
    }
    factory {
        QuestionRepository(get(),get())
    }

    viewModel{
        MainViewModel(get())
    }
    viewModel{
        QuestionViewModel(get())
    }
}