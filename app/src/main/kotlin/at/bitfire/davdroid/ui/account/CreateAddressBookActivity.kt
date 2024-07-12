/*
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 */

package at.bitfire.davdroid.ui.account

import android.accounts.Account
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import at.bitfire.davdroid.R
import at.bitfire.davdroid.db.AppDatabase
import at.bitfire.davdroid.db.Collection
import at.bitfire.davdroid.db.HomeSet
import at.bitfire.davdroid.db.Service
import at.bitfire.davdroid.ui.AppTheme
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.apache.commons.lang3.StringUtils
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class CreateAddressBookActivity: AppCompatActivity() {

    companion object {
        const val EXTRA_ACCOUNT = "account"
    }

    @Inject lateinit var modelFactory: Model.Factory
    val model by viewModels<Model> {
        object: ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val account = intent.getParcelableExtra<Account>(EXTRA_ACCOUNT) ?: throw IllegalArgumentException("EXTRA_ACCOUNT must be set")
                return modelFactory.create(account) as T
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContent {
            AppTheme {
                val displayName by model.displayName.observeAsState()
                val description by model.description.observeAsState()
                val homeSet by model.homeSet.observeAsState()
                val homeSets by model.homeSets.observeAsState()

                Content(
                    isCreateEnabled =
                        displayName != null &&
                        homeSet != null,
                    displayName = displayName,
                    onDisplayNameChange = model.displayName::setValue,
                    description = description,
                    onDescriptionChange = model.description::setValue,
                    homeSet = homeSet,
                    homeSets = homeSets,
                    onHomeSetClicked = model.homeSet::setValue
                )
            }
        }
    }

    override fun supportShouldUpRecreateTask(targetIntent: Intent) = true

    override fun onPrepareSupportNavigateUpTaskStack(builder: TaskStackBuilder) {
        builder.editIntentAt(builder.intentCount - 1)?.putExtra(AccountActivity.EXTRA_ACCOUNT, model.account)
    }


    @Composable
    private fun Content(
        isCreateEnabled: Boolean = false,
        displayName: String? = null,
        onDisplayNameChange: (String) -> Unit = {},
        description: String? = null,
        onDescriptionChange: (String) -> Unit = {},
        homeSet: HomeSet? = null,
        homeSets: List<HomeSet>? = null,
        onHomeSetClicked: (HomeSet) -> Unit = {}
    ) {
        Scaffold(
            topBar = { TopBar(isCreateEnabled) }
        ) { paddingValues ->
            CreateAddressBookForm(
                paddingValues,
                displayName,
                onDisplayNameChange,
                description,
                onDescriptionChange,
                homeSet,
                homeSets,
                onHomeSetClicked
            )
        }
    }

    @Composable
    @Preview(showBackground = true, showSystemUi = true)
    private fun Content_Preview() {
        Content(
            displayName = "Display Name",
            description = "Description",
            homeSets = listOf(
                HomeSet(1, 0, false, "http://example.com/".toHttpUrl()),
                HomeSet(2, 0, false, "http://example.com/".toHttpUrl(), displayName = "Home Set 2"),
            )
        )
    }

    @Composable
    private fun CreateAddressBookForm(
        paddingValues: PaddingValues,
        displayName: String?,
        onDisplayNameChange: (String) -> Unit,
        description: String?,
        onDescriptionChange: (String) -> Unit,
        homeSet: HomeSet?,
        homeSets: List<HomeSet>?,
        onHomeSetClicked: (HomeSet) -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = displayName ?: "",
                onValueChange = onDisplayNameChange,
                label = { Text(stringResource(R.string.create_collection_display_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description ?: "",
                onValueChange = onDescriptionChange,
                label = { Text(stringResource(R.string.create_collection_description_optional)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Text(
                text = stringResource(R.string.create_collection_home_set),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            if (homeSets != null) {
                for (item in homeSets) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = homeSet == item,
                            onClick = { onHomeSetClicked(item) }
                        )
                        Text(
                            text = item.displayName ?: item.url.encodedPath,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBar(isCreateEnabled: Boolean) {
        TopAppBar(
            title = { Text(stringResource(R.string.create_addressbook)) },
            navigationIcon = {
                IconButton(onClick = ::finish) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            },
            actions = {
                IconButton(
                    modifier = Modifier.defaultMinSize(minWidth = 55.dp),
                    enabled = isCreateEnabled,
                    onClick = ::onCreateCollection
                ) {
                    Text(style = MaterialTheme.typography.bodyMedium, overflow = TextOverflow.Visible, maxLines = 1, text = stringResource(R.string.create_collection_create).uppercase())
                }
            }
        )
    }


    private fun onCreateCollection() {
        var ok = true

        val args = Bundle()
        args.putString(CreateCollectionFragment.ARG_SERVICE_TYPE, Service.TYPE_CARDDAV)

        val parent = model.homeSet.value
        if (parent != null) {
            args.putString(
                CreateCollectionFragment.ARG_URL,
                parent.url.resolve(UUID.randomUUID().toString() + "/").toString()
            )
        } else {
            ok = false
        }

        val displayName = model.displayName.value
        if (displayName.isNullOrBlank())
            ok = false
        else
            args.putString(CreateCollectionFragment.ARG_DISPLAY_NAME, displayName)

        StringUtils.trimToNull(model.description.value)?.let {
            args.putString(CreateCollectionFragment.ARG_DESCRIPTION, it)
        }

        if (ok) {
            args.putParcelable(CreateCollectionFragment.ARG_ACCOUNT, model.account)
            args.putString(CreateCollectionFragment.ARG_TYPE, Collection.TYPE_ADDRESSBOOK)
            val frag = CreateCollectionFragment()
            frag.arguments = args
            frag.show(supportFragmentManager, null)
        }
    }


    class Model @AssistedInject constructor(
        val db: AppDatabase,
        @Assisted val account: Account
    ) : ViewModel() {

        @AssistedFactory
        interface Factory {
            fun create(account: Account): Model
        }

        val displayName = MutableLiveData<String>()
        val description = MutableLiveData<String>()
        val homeSets = MutableLiveData<List<HomeSet>>()
        var homeSet = MutableLiveData<HomeSet>()

        init {
            viewModelScope.launch(Dispatchers.IO) {
                // load account info
                db.serviceDao()
                    .getByAccountAndType(account.name, Service.TYPE_CARDDAV)?.let { service ->
                        db.homeSetDao()
                            .getBindableByAccountAndServiceTypeFlow(account.name, service.type)
                            .collect { homesets ->
                                homeSets.postValue(homesets)

                                if (homeSet.value == null)
                                    homeSet.postValue(homesets.firstOrNull())
                            }
                }
            }
        }
    }
}
