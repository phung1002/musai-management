<!-- カレンダー -->
<script setup lang="ts">
import { VCalendar } from "vuetify/labs/VCalendar";
import { ref, onMounted, watch } from "vue";
import { IEvent } from "@/types/type";
import { fetchApprovedLeaveRequests } from "@/api/response";
import { ELeaveType } from "@/constants/leaveType";
import { toast } from "vue3-toastify";
import { useI18n } from "vue-i18n";
const { t } = useI18n();
const events = ref<IEvent[]>([]);
const eventCut = ref<IEvent[]>([]);
const selectedDateEvents = ref<IEvent[]>([]);
const dialog = ref(false);
interface ICalendarDay {
  date: string;
}

const getRandomColor = () => {
  let color = `#${Math.floor(Math.random() * 16777215).toString(16)}`;
  return color.length === 7 ? color : "#000000";
};

const fetchEvents = async () => {
  try {
    const response = await fetchApprovedLeaveRequests();
    const tempEvents: IEvent[] = [];

    response.forEach((item) => {
      const allDay = item.leaveTypeValue == ELeaveType.HALF_DAY ? false : true;
      let currentDate = new Date(item.startDate);
      const endDate = new Date(item.endDate);
      const eventColor = getRandomColor();

      while (currentDate <= endDate) {
        const dayOfWeek = currentDate.getDay();
        if (dayOfWeek !== 0 && dayOfWeek !== 6) {
          tempEvents.push({
            title: item.employeeFullName,
            start: new Date(currentDate),
            end: new Date(currentDate),
            color: eventColor,
            allDay: allDay,
          });
        }
        currentDate.setDate(currentDate.getDate() + 1);
      }
    });
    events.value = tempEvents;
    eventCut.value = events.value.slice(0, 3);
  } catch (error: any) {
    toast.error(t(error.message));
  }
};

const getEventStyle = (color: string) => ({
  backgroundColor: color,
  border: `1px solid ${color}`,
  borderRadius: "4px",
  padding: "2px 4px",
  color: "#fff",
  fontSize: "12px",
  whiteSpace: "nowrap",
  overflow: "hidden",
  textOverflow: "ellipsis",
});

const getDayClass = (date) => {
  const day = date.getDay();
  if (day === 0 || day === 6) {
    return "weekend";
  }
  return "";
};

const getEventsByDate = (date: Date) => {
  return events.value.filter(
    (e) => new Date(e.start).toDateString() === date.toDateString()
  );
};

const handleMoreClick = (date: Date) => {
  selectedDateEvents.value = getEventsByDate(date);
  dialog.value = true;
  let dateStr = date.toDateString();
  renderedDays.delete(dateStr);
};
const renderedDays = new Set<string>();
const shouldDisplayEvent = (day: any, event: any) => {
  let dayEvents = getEventsByDate(new Date(day.date));
  return dayEvents.length && dayEvents[0] === event;
};
watch(events, () => {
  renderedDays.clear();
});
onMounted(fetchEvents);
</script>
<template>
  <VContainer>
    <VCard flat elevation="0">
      <VCardItem>
        <VCalendar
          class="p-2"
          :events="events"
          event-text-color="#fff"
          event-overlap-mode="column"
          color="primary"
          hide-week-number
          :day-class="getDayClass"
        >
          <template #event="{ day, event }">
            <template v-if="shouldDisplayEvent(day, event)">
              <template
                v-for="(ev) in getEventsByDate(new Date((day as ICalendarDay).date)).slice(0, 3)"
                :key="ev.title"
              >
                <VTooltip location="top">
                  <template #activator="{ props }">
                    <div
                      v-bind="props"
                      class="custom-event"
                      :style="getEventStyle(ev.color)"
                    >
                      {{ ev.title }}
                    </div>
                  </template>
                  {{ ev.title }}
                </VTooltip>
              </template>
              <div
                v-if="getEventsByDate(new Date((day as ICalendarDay).date)).length > 3"
                class="more-events"
                @click="handleMoreClick(new Date((day as ICalendarDay).date))"
              >
                <VIcon size="18" icon="mdi-dots-horizontal" />
              </div>
            </template>
          </template>
        </VCalendar>
      </VCardItem>
    </VCard>
    <VDialog v-model="dialog" width="400">
      <VCard>
        <VCardTitle>{{ t("employees_on_leave") }}</VCardTitle>
        <VCardText>
          <div v-for="(ev, i) in selectedDateEvents" :key="i">
            <div :style="getEventStyle(ev.color)">
              {{ ev.title }}
            </div>
          </div>
        </VCardText>
        <VCardActions>
          <VBtn @click="dialog = false">{{ t("close") }}</VBtn>
        </VCardActions>
      </VCard>
    </VDialog>
  </VContainer>
</template>

<style>
.v-calendar-weekly__day-label .bg-primary {
  background-color: inherit !important;
  color: #000 !important;
}
.bg-primary.v-calendar-weekly__day-label__today {
  background-color: #1867c0 !important;
  color: white !important;
  border-radius: 50% !important;
}
.weekend {
  background-color: rgba(255, 0, 0, 0.2) !important;
  color: red !important;
}
.v-calendar-weekly__head-weekday {
  font-weight: bold;
}
.v-calendar-weekly__head-weekday:nth-child(7) {
  background-color: rgba(196, 0, 0, 0.199);
  color: rgb(180, 0, 0) !important;
}
.v-calendar-weekly__head-weekday:nth-child(1) {
  background-color: rgba(255, 0, 0, 0.199);
  color: rgb(180, 0, 0) !important;
}
.v-calendar-weekly__head-weekday:nth-child(n + 2):nth-child(-n + 6) {
  background-color: rgba(0, 86, 247, 0.397);
  color: rgba(0, 14, 136, 0.945) !important;
}
.v-calendar-month__days > .v-calendar-month__day {
  min-height: 100px !important;
}
@media (min-width: 640px) {
  .v-calendar-month__days > .v-calendar-month__day {
    min-height: 150px !important;
  }
}
.v-badge--inline {
  display: none !important;
}
.more-events {
  cursor: pointer;
  color: #1976d2;
  margin-top: 4px;
  text-align: center;
}
.v-card {
  background-color: rgb(var(--v-theme-background));
}
</style>
