<script setup>
</script>

<!--
この<template>ブロックは、アプリケーションのメインUI構造を定義しています。
<header>タグ内では、Vueのロゴ画像とHelloWorldコンポーネントを表示しています。
<img>タグはロゴ画像を表示し、alt属性で代替テキストを指定しています。
<div class="wrapper">はHelloWorldコンポーネントをラップし、msgプロパティでメッセージを渡しています。
<main>タグ内ではTheWelcomeコンポーネントを表示しています。
-->

<template>
  <router-view></router-view>
</template>
