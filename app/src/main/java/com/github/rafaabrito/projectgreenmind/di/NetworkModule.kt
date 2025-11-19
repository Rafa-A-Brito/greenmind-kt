package com.github.rafaabrito.projectgreenmind.di

import com.github.rafaabrito.projectgreenmind.data.services.NominatimApi
import com.github.rafaabrito.projectgreenmind.data.services.OsmService
import com.github.rafaabrito.projectgreenmind.data.services.RealOsmService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    // 1. Binds: Vincula a interface OsmService à sua implementação RealOsmService
    @Binds
    @Singleton
    abstract fun bindOsmService(
        realOsmService: RealOsmService //
    ): OsmService

    companion object {

        // 2. Provides: Cria a instância para logging
        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Útil para ver requests/responses
            }
            return OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        }

        // 3. Provides: Cria a instância do Retrofit (com OkHttpClient)
        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
            // A API do Nominatim (OSM) é usada como BASE_URL para o Geocoding
            val BASE_URL = "https://nominatim.openstreetmap.org/"

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        // 4. Provides: Cria a instância da interface Retrofit
        @Provides
        @Singleton
        fun provideOsmApi(retrofit: Retrofit): NominatimApi {
            // O Retrofit cria a implementação da interface com base nas anotações
            return retrofit.create(NominatimApi::class.java)
        }
    }
}