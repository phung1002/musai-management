<script setup lang="ts">
import { VCalendar } from "vuetify/labs/VCalendar";
import { ref, onMounted } from "vue";
import { IEvent } from "@/types/type";
import { fetchApprovedLeaveRequests } from "@/api/response";
import { ELeaveType } from "@/constants/leaveType";

const events = ref<IEvent[]>([]);
const getRandomColor = () => {
  let color = `#${Math.floor(Math.random() * 16777215).toString(16)}`;
  return color.length === 7 ? color : "#000000";
};
const fetchEvents = async () => {
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
};
const rnd = (a: number, b: number): number => {
  return Math.floor((b - a + 1) * Math.random()) + a;
};
const getDayClass = (date) => {
  const day = date.getDay();
  if (day === 0 || day === 6) {
    return "weekend";
  }
  return "";
};
onMounted(fetchEvents);
</script>

<template>
  <VContainer>
    <VCalendar
      :events="events"
      event-text-color="#fff"
      event-overlap-mode="column"
      color="primary"
      hide-week-number
      :day-class="getDayClass"
    >
    </VCalendar>
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
  color: red !important; /* Red text */
}

/* Add a specific style for event days */
.v-calendar-weekly__head-weekday {
  font-weight: bold;
}

/* (Saturday) */
.v-calendar-weekly__head-weekday:nth-child(7) {
  background-color: rgba(196, 0, 0, 0.199);
  color: rgb(180, 0, 0) !important;
}

/* (Sunday) */
.v-calendar-weekly__head-weekday:nth-child(1) {
  background-color: rgba(255, 0, 0, 0.199);
  color: rgb(180, 0, 0) !important;
}

/* Weekdays */
.v-calendar-weekly__head-weekday:nth-child(n+2):nth-child(-n+6) {
  background-color: rgba(0, 86, 247, 0.2);
  color: rgba(0, 14, 136, 0.945) !important;
}

</style>
