import com.example.advent2025.ds.DeepSeekApi
import com.example.advent2025.ds.OpenAIApi
import com.example.advent2025.ds.OpenRouterApi
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

    fun createDeepSeekApi(): DeepSeekApi = Retrofit.Builder()
        .baseUrl("https://api.deepseek.com/v1/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DeepSeekApi::class.java)

    fun createOpenAIApi(): OpenAIApi = Retrofit.Builder()
        .baseUrl("https://api.openai.com/v1/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenAIApi::class.java)

    fun createOpenRouterApi(): OpenRouterApi = Retrofit.Builder()
        .baseUrl("https://openrouter.ai/api/v1/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenRouterApi::class.java)
}