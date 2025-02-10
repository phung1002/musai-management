<script setup lang="ts">
import { VCalendar } from "vuetify/labs/VCalendar";
</script>

<template>
  <VCard class="my-6 mx-6">
    <VCalendar
      :model-value="new Date()"
      :highlight-dates="[new Date()]"
    >
    </VCalendar>
  </VCard>
</template>

<style>
.v-calendar-weekly__day-label .bg-primary{
  background-color: inherit !important;
  color: #000 !important;
}
.bg-primary.v-calendar-weekly__day-label__today {
  background-color: #1867c0 !important;
  color: white !important;
  border-radius: 50% !important;
}

</style>
