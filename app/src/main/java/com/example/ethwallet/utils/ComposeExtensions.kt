import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.DimenRes
import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.example.ethwallet.utils.BaseViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

fun <T> MutableState<T>.reduce(reducer: T.() -> T) {
    value = reducer.invoke(value)
}

@Composable
fun BackPressHandler(onBackPressed: () -> Unit) {
    // Safely update the current `onBack` lambda when a new one is provided
    val currentOnBackPressed by rememberUpdatedState(onBackPressed)

    // Remember in Composition a back callback that calls the `onBackPressed` lambda
    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }
    }

    backCallback.remove()

    val lifecycle = LocalLifecycleOwner.current
    val backDispatcher = LocalBackPressedDispatcher.current

    // Whenever there's a new dispatcher set up the callback
    DisposableEffect(backDispatcher) {
        backDispatcher.addCallback(lifecycle, backCallback)
        // When the effect leaves the Composition, or there's a new dispatcher, remove the callback
        onDispose {
            backCallback.remove()
        }
    }
}

val LocalBackPressedDispatcher =
    staticCompositionLocalOf<OnBackPressedDispatcher> { error("No Back Dispatcher provided") }


fun <A : BaseViewModel<B, *>, B, C> withState(viewModel: A, block: (B) -> C) =
    block(viewModel.state)

@Composable
@ReadOnlyComposable
fun ssp(@DimenRes id: Int): TextUnit {
    return dimensionResource(id).value.sp
}

/**
 * Creates a Compose State variable that will emit new values whenever this ViewModel's state changes.
 * Prefer the overload with a state property reference to ensure that your composable only recomposes when the properties it uses changes.
 */
@Composable
fun <VM : BaseViewModel<S, *>, S> VM.collectAsState(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): State<S> {
    return remember(stateFlow, lifecycle) {
        stateFlow.flowWithLifecycle(
            lifecycle = lifecycle,
            minActiveState = minActiveState
        )
    }.collectAsState(initial = withState(this) { it })
}
@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun <S> BaseViewModel<S, *>.collectAsStateOptimized(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): State<S> {
    val stateFlowLifecycleAware = stateFlow.flowWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = minActiveState
    )
    return stateFlowLifecycleAware.collectAsState(initial = state)
}
/**
 * Creates a Compose State variable that will emit new values whenever this ViewModel's state mapped to the provided mapper changes.
 * Prefer the overload with a state property reference to ensure that your composable only recomposes when the properties it uses changes.
 */
@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun <VM : BaseViewModel<S, *>, S, A> VM.collectAsState(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    mapper: (S) -> A
): State<A> {
    return remember(stateFlow, lifecycle) {
        stateFlow.flowWithLifecycle(
            lifecycle = lifecycle,
            minActiveState = minActiveState
        )
    }
        .map { mapper(it) }
        .distinctUntilChanged()
        .collectAsState(initial = withState(this) { mapper(it) })
}