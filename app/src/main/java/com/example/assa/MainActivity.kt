package com.example.assa

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.assa.Data.*
import com.example.assa.ViewModels.FilterFormViewModel
import com.example.assa.ViewModels.FinanceViewModel
import com.example.assa.ViewModels.FinancialFormViewModel
import com.example.assa.ViewModels.TotalFinanceViewModel
import com.example.assa.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AssATheme {
                App()
            }
        }
    }
}

@Composable
fun App(
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    val financeRepository = remember { OfflineFinanceDataRepository(financeDao = FinanceDatabase.getDatabase(context).financeDao()) }

    val financeViewModel: FinanceViewModel = remember { FinanceViewModel(repository = financeRepository) }
    val financialFormViewModel: FinancialFormViewModel = remember { FinancialFormViewModel(repository = financeRepository) }
    val totalFinanceViewModel: TotalFinanceViewModel = remember { TotalFinanceViewModel(repository = financeRepository) }
    val filterFormViewModel: FilterFormViewModel = remember { FilterFormViewModel(repository = financeRepository) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    Screen.valueOf(
        backStackEntry?.destination?.route ?: Screen.LOGIN.name
    )

    var startScreen: String
    if (SharedPreferencesManager.getFirstName(context).isNotEmpty() && SharedPreferencesManager.getSurname(context).isNotEmpty()) { // if Name is entered then make financeview the start screen
        startScreen = Screen.FINANCIAL_VIEW.name
    }
    else {
        startScreen = Screen.LOGIN.name
    }

    NavHost(
        navController = navController,
        startDestination = startScreen
    ) {
        composable(route = Screen.LOGIN.name) {
            LogInScreen(navController)
        }
        composable(route = Screen.FINANCIAL_VIEW.name) {
            financialView(financeViewModel,navController, filterFormViewModel)
        }
        composable(route = Screen.FINANCIAL_FORM.name) {
            FinancialFormView(financialFormViewModel, navController)
        }
        composable(route = Screen.FINANCIAL_TOTAL.name) {
            totalFinanceView(totalFinanceViewModel, financeViewModel, filterFormViewModel)
        }
        composable(route = Screen.FILTER_FORM.name) {
            filterFormView(filterFormViewModel, navController, financeViewModel)
        }
    }
}

@Composable
fun LogInScreen(navController: NavHostController) {
    val context = LocalContext.current
    var firstName by rememberSaveable { mutableStateOf("") }
    var surName by rememberSaveable { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(dimensionResource(R.dimen.universal_padding_TextFields))
            ) {
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(stringResource(R.string.firstName_TextField)) }
                )
            }
            Row(
                modifier = Modifier.padding(dimensionResource(R.dimen.universal_padding_TextFields))
            ) {
                TextField(
                    value = surName,
                    onValueChange = { surName = it },
                    label = { Text(stringResource(R.string.surname_TextField)) }
                )
            }
            Box {
                Button(onClick = {
                    SharedPreferencesManager.saveUserNames(context, firstName, surName)
                    navController.navigate(Screen.FINANCIAL_VIEW.name)
                }) {
                    Text(stringResource(R.string.submitButton_text))
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun financialView(
    viewModel: FinanceViewModel,
    navController: NavHostController,
    filterFormViewModel: FilterFormViewModel
) {
    val context = LocalContext.current
    val finances = viewModel.allItems.collectAsState(initial = emptyList())
    val coroutineScope: CoroutineScope = CoroutineScope(rememberCoroutineScope().coroutineContext)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = "${SharedPreferencesManager.getFirstName(context)}${stringResource(R.string.financeView_titleText_text)}",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = dimensionResource(R.dimen.TitleTextSize).value.sp,
                    fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(dimensionResource(R.dimen.OutsideBoxPadding))
            ) {
                Column {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        var selected = viewModel.filtered
                        FilterChip(
                            onClick = {
                                if(filterFormViewModel.hasFilterDates) {
                                    viewModel.filtered = !selected
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.tertiary, containerColor = MaterialTheme.colorScheme.onPrimary),
                            label = {
                                Text(stringResource(R.string.filterButton_text))
                            },
                            selected = selected,
                            leadingIcon = if (selected) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = "Done icon",
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else {
                                null
                            },
                        )
                        var incomeOnly = viewModel.incomeOnly
                        FilterChip(
                            onClick = {
                                if(!viewModel.expenseOnly) {
                                    viewModel.incomeOnly = !incomeOnly
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.tertiary, containerColor = MaterialTheme.colorScheme.onPrimary),
                            label = {
                                Text(stringResource(R.string.incomeButton_Text))
                            },
                            selected = incomeOnly,
                            leadingIcon = if (incomeOnly) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = "Done icon",
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else {
                                null
                            },
                        )
                        var expenseOnly = viewModel.expenseOnly
                        FilterChip(
                            onClick = {
                                if(!viewModel.incomeOnly) {
                                    viewModel.expenseOnly = !expenseOnly
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.tertiary, containerColor = MaterialTheme.colorScheme.onPrimary),
                            label = {
                                Text(stringResource(R.string.expenseButton_Text))
                            },
                            selected = expenseOnly,
                            leadingIcon = if (expenseOnly) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = "Done icon",
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else {
                                null
                            },
                        )
                    }
                    var financeBoxTitle: String
                    if(!viewModel.filtered) {
                        financeBoxTitle = stringResource(R.string.BoxTitle_ShowingAll)
                    }
                    else {
                        financeBoxTitle = stringResource(R.string.BoxTitle_ShowingFiltered)
                    }
                    Text(
                        text = financeBoxTitle,
                        textAlign = TextAlign.Center,
                        fontSize = dimensionResource(R.dimen.financeBox_title_size).value.sp,
                        fontStyle = MaterialTheme.typography.labelSmall.fontStyle,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(dimensionResource(R.dimen.RoundedCorner_dimension))
                            )
                            .padding(dimensionResource(R.dimen.financeBoxPadding_dimen)),
                    ) {
                        var list: State<List<FinanceDataItem>> // display filtered or all financial items
                        if (!viewModel.filtered) {
                            list = finances
                        }
                        else {
                            list = viewModel.filteredList.collectAsState(initial = emptyList())
                        }
                        if(viewModel.incomeOnly) {
                            viewModel.incomeList = listOf(list.value.filter { it.amount > 0 }).asFlow()
                            list = viewModel.incomeList.collectAsState(initial = emptyList())
                        }
                        if (viewModel.expenseOnly) {
                            viewModel.expenseList = listOf(list.value.filter { it.amount < 0 }).asFlow()
                            list = viewModel.expenseList.collectAsState(initial = emptyList())
                        }

                        financialList(list)  // The list of financial items
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
                    .padding(dimensionResource(R.dimen.financeBoxPadding_dimen)),
                contentAlignment = Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column (
                        horizontalAlignment = Alignment.Start
                    ){
                        ElevatedButton(
                            onClick = { navController.navigate(Screen.FINANCIAL_TOTAL.name) },
                        ) {
                            Text(stringResource(R.string.buttonText_ViewTotal_text))
                        }

                        ElevatedButton(
                            onClick = { navController.navigate(Screen.FILTER_FORM.name) }
                        ) {
                            Text(text = stringResource(R.string.buttonText_FilterFinances_text))
                        }
                    }
                    FloatingActionButton(

                        onClick = {
                            /*
                            coroutineScope.launch {
                                viewModel.saveItem(FinanceDataItem(true, LocalDateTime.now(),"test data", FinanceCategory.TRAVEL,-100.0))
                                viewModel.saveItem(FinanceDataItem(false, LocalDateTime.now(),"test data", FinanceCategory.OTHER,250.0))
                                viewModel.saveItem(FinanceDataItem(true, LocalDateTime.now().minusWeeks(2),"test data", FinanceCategory.TRAVEL,-400.0))
                                viewModel.saveItem(FinanceDataItem(false, LocalDateTime.now().minusWeeks(2),"test data", FinanceCategory.OTHER,500.0))
                                viewModel.saveItem(FinanceDataItem(true, LocalDateTime.now().minusMonths(2),"test data", FinanceCategory.TRAVEL,-90.0))
                                viewModel.saveItem(FinanceDataItem(false, LocalDateTime.now().minusMonths(2),"test data", FinanceCategory.OTHER,70.0))
                                viewModel.saveItem(FinanceDataItem(true, LocalDateTime.now().minusMonths(3),"test data", FinanceCategory.TRAVEL,-1005.0))
                            }
                            */


                            /*
                            coroutineScope.launch {
                                viewModel.deleteAllItems()
                            }
                            */

                            navController.navigate(Screen.FINANCIAL_FORM.name) },
                        shape = CircleShape
                    ) {
                        Icon(Icons.Filled.Add, "Button to add financial Data")
                    }
                }
            }
        }
    }
}

@Composable
fun financeItem(financialData: FinanceDataItem) {
    var color: Color
    var costf: String

    var isExpanded by remember {
        mutableStateOf(false)
    }

    if (financialData.expense) {
        color = MaterialTheme.colorScheme.errorContainer
        costf = "${financialData.amount}"
    }
    else {
        color = MaterialTheme.colorScheme.inversePrimary
        costf = "+${financialData.amount}"
    }

    val catIcon: ImageVector = when (financialData.category) {
        FinanceCategory.OTHER -> Icons.Rounded.ArrowForward
        FinanceCategory.ACCOMODATION -> Icons.Rounded.Home
        FinanceCategory.LEISURE -> Icons.Rounded.Favorite
        FinanceCategory.FOOD -> Icons.Rounded.ShoppingCart
        FinanceCategory.TRAVEL -> Icons.Rounded.Place
    }

    Row(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.financeItemRowPadding))
    ) {
        Column(
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
        ) {
            Row {
                Icon(
                    catIcon,"Category Icon",
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = if (isExpanded) { "${financialData.title}\nAmount: ${costf}\nCategory: ${financialData.category.name.toLowerCase()}\nDate: ${financialData.date.dayOfMonth} - ${financialData.date.month} - ${financialData.date.year}" } else {"${costf}"},
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = dimensionResource(R.dimen.financeItem_fontSize).value.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (isExpanded) {Int.MAX_VALUE} else 1,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color,
                            RoundedCornerShape(dimensionResource(R.dimen.RoundedCorner_dimension))
                        )
                        .padding(dimensionResource(R.dimen.financeItem_ItemPadding))
                        .animateContentSize(),
                    textAlign = if (isExpanded) { TextAlign.Start } else { TextAlign.Center }
                )
            }
        }
    }
}

@Composable
fun financialList(financialData: State<List<FinanceDataItem>>) {
    LazyColumn() {
        itemsIndexed(financialData.value) { idx, row -> financeItem(financialData = row) }
    }
}

@Composable
fun FinancialFormView(
    viewModel: FinancialFormViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    var checkedExpense = viewModel.expense
    val coroutineScope = rememberCoroutineScope()
    var title = viewModel.title
    var amount = viewModel.amount

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ){
                Text(
                    text = "${stringResource(R.string.financialFormView_title_text)} ${SharedPreferencesManager.getFirstName(context)} ${SharedPreferencesManager.getSurname(context)}",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = dimensionResource(R.dimen.FinanceForm_title_fontsize).value.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.universal_padding_TextFields))
                )
            }
            Row {
                Text(
                    text = stringResource(R.string.switch_expense_text),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(dimensionResource(R.dimen.universal_padding_TextFields))
                )
                Switch(
                    checked = checkedExpense,
                    onCheckedChange = {newState ->
                        viewModel.expense = newState
                    }
                )
            }
            TextField(
                value = title,
                modifier = Modifier.padding(dimensionResource(R.dimen.universal_padding_TextFields)),
                onValueChange = { newTitle ->
                    viewModel.title = newTitle
                },
                label = { Text(stringResource(R.string.financeForm_TextField_Title)) }
            )
            TextField(
                value = amount.toString(),
                modifier = Modifier.padding(dimensionResource(R.dimen.universal_padding_TextFields)),
                onValueChange = { newAmount ->
                    viewModel.amount = newAmount
                },
                label = { Text(stringResource(R.string.financeForm_TextField_amount)) }
            )

            CategoriesExposedDropdownMenuBox(viewModel)
            
            Button(
                modifier = Modifier.padding(dimensionResource(R.dimen.universal_padding_TextFields),0.dp),
                onClick = {
                coroutineScope.launch {
                    if(viewModel.title.isNotEmpty() && viewModel.amount.isNotEmpty() && viewModel.amount.toDoubleOrNull() != null) { // check for input being correct, submit to database, navigate back to financialview
                        viewModel.submitForm()
                        navController.navigate(Screen.FINANCIAL_VIEW.name)
                    }
                }
            }) {
                Text(
                    text = stringResource(R.string.financialForm_ButtonText_Add)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesExposedDropdownMenuBox(vm: FinancialFormViewModel) {
    val context = LocalContext.current
    val categories = arrayOf("${FinanceCategory.TRAVEL.name}", "${FinanceCategory.FOOD.name}", "${FinanceCategory.LEISURE.name}", "${FinanceCategory.ACCOMODATION.name}", "${FinanceCategory.OTHER.name}")
    var expanded by remember { mutableStateOf(false) }
    var selectedText = vm.financeCategory.name

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.universal_padding_TextFields))
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                    expanded = !expanded
            }
        ) {
            TextField(
                value = selectedText,
                onValueChange = { newState ->
                    vm.financeCategory = FinanceCategory.valueOf(newState)
                },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedText = item
                            expanded = false
                            vm.financeCategory = FinanceCategory.valueOf(selectedText)
                        }
                    )
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun totalFinanceView(
    viewModel: TotalFinanceViewModel,
    financeViewModel: FinanceViewModel,
    filterFormViewModel: FilterFormViewModel
) {
    var totalunfiltered = viewModel.total.collectAsState(initial = emptyFlow<Double>())
    val context = LocalContext.current
    var usedSum: State<Any>

    if (!financeViewModel.filtered) {
        usedSum = totalunfiltered
    }
    else {
        usedSum = viewModel.getFilteredSum(filterFormViewModel.formattedDateOne, filterFormViewModel.formattedDateTwo).collectAsState(initial = emptyFlow<Double>())
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                text = "${SharedPreferencesManager.getFirstName(context)} ${SharedPreferencesManager.getSurname(context)} ${stringResource(R.string.totalfinanceView_titleText)}",
                textAlign = TextAlign.Center
            )
            RoundBox(RoundedCornerShape(dimensionResource(R.dimen.RoundedCorner_dimension)),usedSum)
        }
    }
}

@Composable
fun RoundBox(shape: Shape, total: State<Any>){
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentSize(Center)
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.RoundBox_size))
                .clip(shape)
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Center
        ) {
            Text(
                text = "$ ${total.value}",
                fontSize = MaterialTheme.typography.displayLarge.fontSize,
                fontStyle = MaterialTheme.typography.displayLarge.fontStyle,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun filterFormView(
    viewModel: FilterFormViewModel,
    navController: NavController,
    financeViewModel: FinanceViewModel
) {
    val context = LocalContext.current
    var firstDate = viewModel.firstDate
    var secondDate = viewModel.secondDate

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally
        ) {
            Text(text = stringResource(R.string.filterFormView_title))
            Row(
                modifier = Modifier.padding(dimensionResource(R.dimen.universal_padding_TextFields))
            ) {
                TextField(
                    value = firstDate,
                    onValueChange = {newFirstDate ->
                        viewModel.firstDate = newFirstDate
                                    },
                    label = { Text(stringResource(R.string.filterFormView_startDate_text)) }
                )
            }
            Row(
                modifier = Modifier.padding(dimensionResource(R.dimen.universal_padding_TextFields))
            ) {
                TextField(
                    value = secondDate,
                    onValueChange = { newSecondDate ->
                        viewModel.secondDate = newSecondDate
                                    },
                    label = { Text(text = stringResource(R.string.filterFormView_endDate_text)) }
                )
            }
            Box {
                Button(onClick = {
                    financeViewModel.filtered = true
                    financeViewModel.filteredList = viewModel.filterDates()
                    viewModel.hasFilterDates = true
                    navController.navigate(Screen.FINANCIAL_VIEW.name)
                }) {
                    Text(text = stringResource(R.string.filterFormView_filter_text))
                }
            }
        }
    }
}
