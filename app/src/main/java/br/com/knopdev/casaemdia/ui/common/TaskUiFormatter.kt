package br.com.knopdev.casaemdia.ui.common

import android.content.Context
import br.com.knopdev.casaemdia.R
import br.com.knopdev.casaemdia.model.TaskCategory
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun TaskCategory.displayName(context: Context): String = context.getString(
    when (this) {
        TaskCategory.CLEANING -> R.string.category_cleaning
        TaskCategory.MAINTENANCE -> R.string.category_maintenance
        TaskCategory.BILLS -> R.string.category_bills
        TaskCategory.SHOPPING -> R.string.category_shopping
        TaskCategory.OTHER -> R.string.category_other
    }
)

fun formatDueAt(context: Context, dueAtEpochMillis: Long?): String {
    if (dueAtEpochMillis == null) return context.getString(R.string.no_due_date)

    val zone = ZoneId.systemDefault()
    val due = Instant.ofEpochMilli(dueAtEpochMillis).atZone(zone)
    val today = LocalDate.now(zone)
    val time = due.format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))
    return when (due.toLocalDate()) {
        today -> context.getString(R.string.due_today_at, time)
        today.plusDays(1) -> context.getString(R.string.due_tomorrow_at, time)
        else -> due.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm", Locale.getDefault()))
    }
}

fun isOverdue(dueAtEpochMillis: Long?, completed: Boolean): Boolean =
    !completed && dueAtEpochMillis != null && dueAtEpochMillis < System.currentTimeMillis()
