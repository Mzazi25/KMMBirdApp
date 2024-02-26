import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import models.BirdImage

data class BirdsUiState(
    val birdsImage: List<BirdImage> = emptyList(),
    val selectedCategory: String? = null
){
    val category = birdsImage.map { it.category }.toSet()
    val selectedImage = birdsImage.filter { it.category == selectedCategory }
}

class BirdsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BirdsUiState())
    val uiState =_uiState.asStateFlow()


    private val httpClient = HttpClient {
        install(ContentNegotiation){
            json()
        }
    }

    init {
        updateImages()
    }

    fun selectedCategory(selectedCategory: String?){
        _uiState.update {
            it.copy(selectedCategory = selectedCategory)
        }
    }

    override fun onCleared() {
        httpClient.close()
    }
    private fun updateImages(){
        viewModelScope.launch {
            val images = getImage()
            _uiState.update {
                it.copy(birdsImage = images)
            }
        }
    }
    private suspend fun getImage():List<BirdImage>{
        val image =   httpClient
            .get("https://sebi.io/demo-image-api/pictures.json")
            .body<List<BirdImage>>()
        return image
    }
}

