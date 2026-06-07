<template>
  <image 
    :src="localSrc" 
    :mode="mode"
    :class="['remote-image', customClass]"
    :style="customStyle"
    @error="handleError"
    @load="handleLoad"
  />
</template>

<script>
import { getImageUrl } from '../utils/image.js'

export default {
  name: 'RemoteImage',
  props: {
    src: {
      type: String,
      required: true
    },
    mode: {
      type: String,
      default: 'aspectFill'
    },
    // 支持class传递
    customClass: {
      type: String,
      default: ''
    },
    // 支持style传递
    customStyle: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      localSrc: '/static/images/placeholder.png',
      loading: false
    }
  },
  watch: {
    src: {
      immediate: true,
      handler(newSrc) {
        this.loadImage(newSrc)
      }
    }
  },
  methods: {
    async loadImage(url) {
      this.localSrc = getImageUrl(url)
    },
    
    handleError(e) {
      console.error('[图片加载失败]', this.src, e)
      this.$emit('error', e)
    },
    
    handleLoad(e) {
      this.$emit('load', e)
    }
  }
}
</script>

<style scoped>
.remote-image {
  display: block;
}
</style>
