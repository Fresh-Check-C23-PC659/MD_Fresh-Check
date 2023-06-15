import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.freshcheck.ui.viewmodel.LoginViewModel
import com.example.freshcheck.ui.viewmodel.MainCategoryViewModel
import com.example.freshcheck.ui.viewmodel.ProfileViewModel
import com.example.freshcheck.ui.viewmodel.RegisterViewModel
import java.lang.ref.WeakReference

class ViewModelFactory private constructor(context: Context) : ViewModelProvider.Factory {
    private val contextRef: WeakReference<Context> = WeakReference(context)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val context = contextRef.get()
            ?: throw IllegalStateException("Context has been garbage collected")

        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel() as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(context) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel() as T
            }
            modelClass.isAssignableFrom(MainCategoryViewModel::class.java) -> {
                MainCategoryViewModel() as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.simpleName}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(context.applicationContext).also { instance = it }
            }
        }
    }
}
