
import com.example.advent2025.ds.OpenRouterApi
import com.example.advent2025.ds.YandexApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("HTTP-Referer", "https://advent2025.com")
            .addHeader("X-Title", "LLM Chat App")
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    fun createOpenRouterApi(): OpenRouterApi = Retrofit.Builder()
        .baseUrl("https://openrouter.ai/api/v1/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenRouterApi::class.java)

    fun createYandexApi(): YandexApi = Retrofit.Builder()
        .baseUrl("https://llm.api.cloud.yandex.net/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(YandexApi::class.java)

}