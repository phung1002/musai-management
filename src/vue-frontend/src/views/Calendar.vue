<!-- カレンダー -->
<script setup lang="ts">
import { VCalendar } from "vuetify/labs/VCalendar";
import { ref, onMounted } from "vue";
import { IEvent } from "@/types/type";
import { fetchApprovedLeaveRequests } from "@/api/response";
import { ELeaveType } from "@/constants/leaveType";

// イベントリストを格納するためのrefを定義
const events = ref<IEvent[]>([]);

// ランダムな色を生成する関数
const getRandomColor = () => {
  let color = `#${Math.floor(Math.random() * 16777215).toString(16)}`;
  return color.length === 7 ? color : "#000000"; // 6桁のカラーコードが生成されなければ、デフォルトで黒を返す
};

// イベントデータを取得する非同期関数
const fetchEvents = async () => {
  const response = await fetchApprovedLeaveRequests(); // 承認された休暇リクエストを取得
  const tempEvents: IEvent[] = []; // 一時的にイベントデータを格納する配列

  response.forEach((item) => {
    const allDay = item.leaveTypeValue == ELeaveType.HALF_DAY ? false : true; // 半日休暇の場合は終日フラグをfalseに設定
    let currentDate = new Date(item.startDate); // 休暇開始日をcurrentDateに設定
    const endDate = new Date(item.endDate); // 休暇終了日をendDateに設定
    const eventColor = getRandomColor(); // ランダムな色を生成

    // 休暇期間内の日付を1日ずつ処理
    while (currentDate <= endDate) {
      const dayOfWeek = currentDate.getDay(); // 現在の日付が週の何日かを取得

      // 土日（0:日曜日、6:土曜日）はスキップ
      if (dayOfWeek !== 0 && dayOfWeek !== 6) {
        // イベントをtempEventsに追加
        tempEvents.push({
          title: item.userFullName, // イベントのタイトル（ユーザー名）
          start: new Date(currentDate), // イベントの開始日時
          end: new Date(currentDate), // イベントの終了日時
          color: eventColor, // ランダムで生成した色
          allDay: allDay, // 終日かどうか
        });
      }

      // 次の日に進める
      currentDate.setDate(currentDate.getDate() + 1);
    }
  });
  events.value = tempEvents; // イベントリストに取得したイベントをセット
};

// 指定された範囲内でランダムな数字を生成する関数
const rnd = (a: number, b: number): number => {
  return Math.floor((b - a + 1) * Math.random()) + a; // aからbの範囲でランダムな整数を生成
};

// 日付にクラスを設定する関数
const getDayClass = (date) => {
  const day = date.getDay(); // 曜日を取得
  if (day === 0 || day === 6) {
    return "weekend"; // 土日なら"weekend"クラスを返す
  }
  return ""; // 平日ならクラスを返さない
};

// コンポーネントがマウントされた際にイベントを取得
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
.v-calendar-weekly__head-weekday:nth-child(n + 2):nth-child(-n + 6) {
  background-color: rgba(0, 86, 247, 0.2);
  color: rgba(0, 14, 136, 0.945) !important;
}
</style>
