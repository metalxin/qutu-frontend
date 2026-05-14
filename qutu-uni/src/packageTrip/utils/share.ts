import { BASE_URL } from '@/api/request'

export interface ShareConfig {
  title: string
  path: string
  imageUrl?: string
  summary?: string
}

export interface PosterConfig {
  coverUrl: string
  title: string
  subtitle?: string
  rating?: number
  location?: string
  price?: string
  tags?: string[]
  qrCodePath?: string
  appName?: string
  slogan?: string
}

const APP_NAME = '趣途云迹'
const APP_SLOGAN = '发现世界的每一处美好'

function getSharePath(type: string, id: number | string): string {
  return `/packageTrip/pages/destination/detail?id=${id}&from=share`
}

export function buildShareConfig(type: string, id: number | string, title: string, imageUrl?: string, summary?: string): ShareConfig {
  return {
    title: `${title} - ${APP_NAME}`,
    path: getSharePath(type, id),
    imageUrl: imageUrl || '',
    summary: summary || ''
  }
}

export function shareToWechatFriend(config: ShareConfig) {
  trackShare('wechat_friend', config.path)
}

export function shareToMoments(config: ShareConfig) {
  trackShare('moments', config.path)
}

export async function copyShareLink(path: string): Promise<void> {
  const fullPath = `${BASE_URL}${path}`
  return new Promise((resolve, reject) => {
    uni.setClipboardData({
      data: fullPath,
      success: () => {
        trackShare('copy_link', path)
        resolve()
      },
      fail: reject
    })
  })
}

export async function downloadNetworkImage(url: string): Promise<string> {
  if (!url) return ''
  return new Promise((resolve, reject) => {
    uni.downloadFile({
      url,
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.tempFilePath)
        } else {
          reject(new Error(`图片下载失败(HTTP ${res.statusCode})`))
        }
      },
      fail: (err) => {
        console.error('downloadFile fail:', err)
        reject(new Error('图片下载失败，请检查网络或小程序域名配置'))
      }
    })
  })
}

export async function saveImageToAlbum(filePath: string): Promise<void> {
  return new Promise((resolve, reject) => {
    uni.saveImageToPhotosAlbum({
      filePath,
      success: () => resolve(),
      fail: (err) => {
        if (err.errMsg?.includes('auth deny') || err.errMsg?.includes('authorize')) {
          uni.showModal({
            title: '需要相册权限',
            content: '请在设置中允许访问相册，以保存分享海报',
            confirmText: '去设置',
            success: (res) => {
              if (res.confirm) {
                uni.openSetting()
              }
            }
          })
        }
        reject(err)
      }
    })
  })
}

function drawRoundRect(ctx: CanvasRenderingContext2D, x: number, y: number, w: number, h: number, r: number) {
  ctx.beginPath()
  ctx.moveTo(x + r, y)
  ctx.lineTo(x + w - r, y)
  ctx.arcTo(x + w, y, x + w, y + r, r)
  ctx.lineTo(x + w, y + h - r)
  ctx.arcTo(x + w, y + h, x + w - r, y + h, r)
  ctx.lineTo(x + r, y + h)
  ctx.arcTo(x, y + h, x, y + h - r, r)
  ctx.lineTo(x, y + r)
  ctx.arcTo(x, y, x + r, y, r)
  ctx.closePath()
}

function drawTextWithEllipsis(ctx: CanvasRenderingContext2D, text: string, x: number, y: number, maxWidth: number, fontSize: number, fontWeight: string = 'normal') {
  ctx.font = `${fontWeight} ${fontSize}px -apple-system, "PingFang SC", "Helvetica Neue", sans-serif`
  let displayText = text
  const measured = ctx.measureText(displayText).width
  if (measured <= maxWidth) {
    ctx.fillText(displayText, x, y)
    return displayText
  }
  while (ctx.measureText(displayText + '...').width > maxWidth && displayText.length > 0) {
    displayText = displayText.slice(0, -1)
  }
  displayText += '...'
  ctx.fillText(displayText, x, y)
  return displayText
}

function drawTag(ctx: CanvasRenderingContext2D, text: string, x: number, y: number, fontSize: number, bgColor: string, textColor: string): number {
  ctx.font = `${fontSize}px -apple-system, "PingFang SC", "Helvetica Neue", sans-serif`
  const padding = 8
  const tagWidth = ctx.measureText(text).width + padding * 2
  const tagHeight = fontSize + padding * 2
  drawRoundRect(ctx, x, y, tagWidth, tagHeight, tagHeight / 2)
  ctx.fillStyle = bgColor
  ctx.fill()
  ctx.fillStyle = textColor
  ctx.fillText(text, x + padding, y + tagHeight / 2 + fontSize / 3)
  return tagWidth
}

export async function generatePoster(
  canvasId: string,
  config: PosterConfig,
  componentInstance?: any
): Promise<string> {
  const systemInfo = uni.getSystemInfoSync()
  const canvasWidth = 600
  const canvasHeight = 900
  const dpr = systemInfo.pixelRatio || 2

  let canvasRes: any = null
  for (let attempt = 0; attempt < 3; attempt++) {
    const query = (uni.createSelectorQuery() as any).in(componentInstance)
    canvasRes = await new Promise<any>((resolve, reject) => {
      query.select(`#${canvasId}`)
        .fields({ node: true, size: true })
        .exec((res: any) => {
          if (res && res[0] && res[0].node) {
            resolve(res[0])
          } else {
            reject(new Error(`Canvas节点未找到(尝试${attempt + 1}/3)`))
          }
        })
    }).catch(() => null)

    if (canvasRes) break
    await new Promise(resolve => setTimeout(resolve, 300))
  }

  if (!canvasRes || !canvasRes.node) {
    throw new Error('Canvas节点未找到，请确保Canvas已渲染')
  }

  const canvas = canvasRes.node
  canvas.width = canvasWidth * dpr
  canvas.height = canvasHeight * dpr

  const ctx = canvas.getContext('2d') as CanvasRenderingContext2D
  ctx.scale(dpr, dpr)

  const white = '#FFFFFF'
  const primary = '#007AFF'
  const textDark = '#1D1D1F'
  const textSecondary = '#86868B'
  const textLight = '#AEAEB2'
  const bgLight = '#F5F5F7'

  ctx.fillStyle = white
  ctx.fillRect(0, 0, canvasWidth, canvasHeight)

  let coverPath = ''
  try {
    if (config.coverUrl) {
      coverPath = await downloadNetworkImage(config.coverUrl)
    }
  } catch (e) {
    console.warn('封面图下载失败，使用默认背景', e)
  }

  const coverHeight = 420
  const coverX = 0
  const coverY = 0

  if (coverPath) {
    try {
      const image = canvas.createImage()
      await new Promise<void>((resolve, reject) => {
        image.onload = () => resolve()
        image.onerror = () => reject(new Error('图片加载失败'))
        image.src = coverPath
      })
      const imgRatio = image.width / image.height
      const targetRatio = canvasWidth / coverHeight
      let sx = 0, sy = 0, sw = image.width, sh = image.height
      if (imgRatio > targetRatio) {
        sw = image.height * targetRatio
        sx = (image.width - sw) / 2
      } else {
        sh = image.width / targetRatio
        sy = (image.height - sh) / 2
      }
      ctx.drawImage(image, sx, sy, sw, sh, coverX, coverY, canvasWidth, coverHeight)

      const gradient = ctx.createLinearGradient(0, coverY + coverHeight - 120, 0, coverY + coverHeight)
      gradient.addColorStop(0, 'rgba(0,0,0,0)')
      gradient.addColorStop(1, 'rgba(0,0,0,0.4)')
      ctx.fillStyle = gradient
      ctx.fillRect(0, coverY + coverHeight - 120, canvasWidth, 120)
    } catch (e) {
      ctx.fillStyle = primary
      ctx.fillRect(coverX, coverY, canvasWidth, coverHeight)
      ctx.fillStyle = white
      ctx.font = `bold 32px -apple-system, "PingFang SC", "Helvetica Neue", sans-serif`
      ctx.textAlign = 'center'
      ctx.fillText(APP_NAME, canvasWidth / 2, coverHeight / 2)
      ctx.textAlign = 'left'
    }
  } else {
    ctx.fillStyle = primary
    ctx.fillRect(coverX, coverY, canvasWidth, coverHeight)
    ctx.fillStyle = white
    ctx.font = `bold 32px -apple-system, "PingFang SC", "Helvetica Neue", sans-serif`
    ctx.textAlign = 'center'
    ctx.fillText(APP_NAME, canvasWidth / 2, coverHeight / 2)
    ctx.textAlign = 'left'
  }

  let currentY = coverHeight + 48
  const contentPadding = 40
  const contentWidth = canvasWidth - contentPadding * 2

  ctx.fillStyle = textDark
  drawTextWithEllipsis(ctx, config.title, contentPadding, currentY, contentWidth, 36, 'bold')
  currentY += 52

  if (config.subtitle) {
    ctx.fillStyle = textSecondary
    drawTextWithEllipsis(ctx, config.subtitle, contentPadding, currentY, contentWidth, 28)
    currentY += 44
  }

  if (config.rating !== undefined && config.rating > 0) {
    const starY = currentY
    ctx.font = `28px -apple-system, "PingFang SC", "Helvetica Neue", sans-serif`
    const fullStars = Math.floor(config.rating)
    const halfStar = config.rating % 1 >= 0.5
    const stars = '★'.repeat(fullStars) + (halfStar ? '☆' : '')
    ctx.fillStyle = '#FFB800'
    ctx.fillText(stars, contentPadding, starY)
    const starWidth = ctx.measureText(stars).width
    ctx.fillStyle = textDark
    ctx.font = `bold 28px -apple-system, "PingFang SC", "Helvetica Neue", sans-serif`
    ctx.fillText(`${config.rating}`, contentPadding + starWidth + 10, starY)
    currentY += 44
  }

  if (config.location) {
    ctx.fillStyle = textSecondary
    ctx.font = `26px -apple-system, "PingFang SC", "Helvetica Neue", sans-serif`
    ctx.fillText(`📍 ${config.location}`, contentPadding, currentY)
    currentY += 38
  }

  if (config.price) {
    ctx.fillStyle = '#34C759'
    ctx.font = `bold 30px -apple-system, "PingFang SC", "Helvetica Neue", sans-serif`
    ctx.fillText(config.price, contentPadding, currentY)
    currentY += 48
  }

  if (config.tags && config.tags.length > 0) {
    let tagX = contentPadding
    const tagY = currentY - 24
    const displayTags = config.tags.slice(0, 4)
    for (const tag of displayTags) {
      const tagWidth = drawTag(ctx, tag, tagX, tagY, 22, bgLight, textSecondary)
      tagX += tagWidth + 10
      if (tagX > canvasWidth - contentPadding - 60) break
    }
    currentY = tagY + 52
  }

  const footerY = canvasHeight - 100
  ctx.fillStyle = bgLight
  ctx.fillRect(0, footerY, canvasWidth, 100)

  ctx.strokeStyle = '#E5E5EA'
  ctx.lineWidth = 1
  ctx.beginPath()
  ctx.moveTo(contentPadding, footerY)
  ctx.lineTo(canvasWidth - contentPadding, footerY)
  ctx.stroke()

  ctx.fillStyle = textDark
  ctx.font = `bold 26px -apple-system, "PingFang SC", "Helvetica Neue", sans-serif`
  ctx.fillText(config.appName || APP_NAME, contentPadding, footerY + 40)

  ctx.fillStyle = textLight
  ctx.font = `22px -apple-system, "PingFang SC", "Helvetica Neue", sans-serif`
  ctx.fillText(config.slogan || APP_SLOGAN, contentPadding, footerY + 68)

  ctx.fillStyle = textLight
  ctx.font = `20px -apple-system, "PingFang SC", "Helvetica Neue", sans-serif`
  ctx.textAlign = 'right'
  ctx.fillText('长按识别小程序码', canvasWidth - contentPadding, footerY + 54)
  ctx.textAlign = 'left'

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      uni.canvasToTempFilePath({
        canvasId,
        canvas,
        x: 0,
        y: 0,
        width: canvasWidth,
        height: canvasHeight,
        destWidth: canvasWidth * dpr,
        destHeight: canvasHeight * dpr,
        success: (res: any) => {
          resolve(res.tempFilePath)
        },
        fail: (err: any) => {
          console.error('canvasToTempFilePath 失败', err)
          reject(err)
        }
      } as any, componentInstance)
    }, 500)
  })
}

export async function generateAndSavePoster(
  canvasId: string,
  config: PosterConfig,
  componentInstance?: any
): Promise<string> {
  uni.showLoading({ title: '生成海报中...' })
  try {
    const filePath = await generatePoster(canvasId, config, componentInstance)
    uni.hideLoading()
    return filePath
  } catch (e: any) {
    uni.hideLoading()
    uni.showToast({ title: '海报生成失败', icon: 'none' })
    throw e
  }
}

/**
 * 生成攻略分享海报
 */
export interface GuidePosterConfig {
  coverUrl: string
  title: string
  description: string
  city?: string
  days?: string
  tag?: string
  likes?: number
  views?: number
  guideId: number | string
}

export async function generateGuidePoster(
  canvasId: string,
  config: GuidePosterConfig,
  componentInstance?: any
): Promise<string> {
  const systemInfo = uni.getSystemInfoSync()
  const canvasWidth = 600
  const canvasHeight = 960
  const dpr = systemInfo.pixelRatio || 2

  let canvasRes: any = null
  for (let attempt = 0; attempt < 3; attempt++) {
    const query = (uni.createSelectorQuery() as any).in(componentInstance)
    canvasRes = await new Promise<any>((resolve, reject) => {
      query.select(`#${canvasId}`)
        .fields({ node: true, size: true })
        .exec((res: any) => {
          if (res && res[0] && res[0].node) {
            resolve(res[0])
          } else {
            reject(new Error(`Canvas节点未找到(尝试${attempt + 1}/3)`))
          }
        })
    }).catch(() => null)
    if (canvasRes) break
    await new Promise(resolve => setTimeout(resolve, 300))
  }

  if (!canvasRes || !canvasRes.node) {
    throw new Error('Canvas节点未找到')
  }

  const canvas = canvasRes.node
  canvas.width = canvasWidth * dpr
  canvas.height = canvasHeight * dpr
  const ctx = canvas.getContext('2d') as CanvasRenderingContext2D
  ctx.scale(dpr, dpr)

  const white = '#FFFFFF'
  const textDark = '#1D1D1F'
  const textSecondary = '#86868B'
  const primary = '#00C853'
  const bgLight = '#F5F5F7'

  // 背景
  ctx.fillStyle = white
  drawRoundRect(ctx, 0, 0, canvasWidth, canvasHeight, 24)
  ctx.fill()

  // 封面图
  let coverPath = ''
  try {
    if (config.coverUrl) {
      coverPath = await downloadNetworkImage(config.coverUrl)
    }
  } catch (e) {
    console.warn('封面图下载失败', e)
  }

  const coverHeight = 480
  if (coverPath) {
    try {
      const image = canvas.createImage()
      await new Promise<void>((resolve, reject) => {
        image.onload = () => resolve()
        image.onerror = () => reject(new Error('图片加载失败'))
        image.src = coverPath
      })
      const imgRatio = image.width / image.height
      const targetRatio = canvasWidth / coverHeight
      let sx = 0, sy = 0, sw = image.width, sh = image.height
      if (imgRatio > targetRatio) {
        sw = image.height * targetRatio
        sx = (image.width - sw) / 2
      } else {
        sh = image.width / targetRatio
        sy = (image.height - sh) / 2
      }
      // 圆角裁切顶部
      ctx.save()
      drawRoundRect(ctx, 0, 0, canvasWidth, coverHeight + 24, 24)
      ctx.clip()
      ctx.drawImage(image, sx, sy, sw, sh, 0, 0, canvasWidth, coverHeight)
      // 底部渐变
      const gradient = ctx.createLinearGradient(0, coverHeight - 120, 0, coverHeight)
      gradient.addColorStop(0, 'rgba(0,0,0,0)')
      gradient.addColorStop(1, 'rgba(0,0,0,0.3)')
      ctx.fillStyle = gradient
      ctx.fillRect(0, coverHeight - 120, canvasWidth, 120)
      ctx.restore()
    } catch (e) {
      ctx.fillStyle = primary
      ctx.fillRect(0, 0, canvasWidth, coverHeight)
      ctx.fillStyle = white
      ctx.font = `bold 32px -apple-system, "PingFang SC", sans-serif`
      ctx.textAlign = 'center'
      ctx.fillText(APP_NAME, canvasWidth / 2, coverHeight / 2)
      ctx.textAlign = 'left'
    }
  } else {
    ctx.fillStyle = primary
    ctx.fillRect(0, 0, canvasWidth, coverHeight)
    ctx.fillStyle = white
    ctx.font = `bold 32px -apple-system, "PingFang SC", sans-serif`
    ctx.textAlign = 'center'
    ctx.fillText(APP_NAME, canvasWidth / 2, coverHeight / 2)
    ctx.textAlign = 'left'
  }

  let currentY = coverHeight + 48
  const padding = 40
  const contentWidth = canvasWidth - padding * 2

  // 标题
  ctx.fillStyle = textDark
  drawTextWithEllipsis(ctx, config.title, padding, currentY, contentWidth, 36, 'bold')
  currentY += 52

  // 描述
  if (config.description) {
    ctx.fillStyle = textSecondary
    drawTextWithEllipsis(ctx, config.description, padding, currentY, contentWidth, 26)
    currentY += 40
  }

  // 标签行
  let tagX = padding
  const tagY = currentY - 20
  if (config.city) {
    tagX += drawTag(ctx, config.city, tagX, tagY, 22, 'rgba(0,200,83,0.12)', primary) + 10
  }
  if (config.days) {
    tagX += drawTag(ctx, config.days, tagX, tagY, 22, 'rgba(0,200,83,0.12)', primary) + 10
  }
  if (config.tag) {
    drawTag(ctx, config.tag, tagX, tagY, 22, 'rgba(0,200,83,0.12)', primary)
  }
  currentY = tagY + 52

  // 统计
  if (config.likes || config.views) {
    ctx.fillStyle = textSecondary
    ctx.font = `24px -apple-system, "PingFang SC", sans-serif`
    let statsText = ''
    if (config.likes) statsText += `${config.likes} 赞`
    if (config.likes && config.views) statsText += '  ·  '
    if (config.views) statsText += `${config.views} 浏览`
    ctx.fillText(statsText, padding, currentY)
    currentY += 40
  }

  // 分割线
  ctx.strokeStyle = '#E5E5EA'
  ctx.lineWidth = 1
  ctx.beginPath()
  ctx.moveTo(padding, currentY)
  ctx.lineTo(canvasWidth - padding, currentY)
  ctx.stroke()
  currentY += 32

  // 底部品牌
  const footerY = canvasHeight - 100
  ctx.fillStyle = bgLight
  ctx.fillRect(0, footerY, canvasWidth, 100)

  ctx.fillStyle = textDark
  ctx.font = `bold 26px -apple-system, "PingFang SC", sans-serif`
  ctx.fillText(APP_NAME, padding, footerY + 40)

  ctx.fillStyle = '#AEAEB2'
  ctx.font = `22px -apple-system, "PingFang SC", sans-serif`
  ctx.fillText(APP_SLOGAN, padding, footerY + 68)

  ctx.fillStyle = '#AEAEB2'
  ctx.font = `20px -apple-system, "PingFang SC", sans-serif`
  ctx.textAlign = 'right'
  ctx.fillText('长按识别小程序码', canvasWidth - padding, footerY + 54)
  ctx.textAlign = 'left'

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      uni.canvasToTempFilePath({
        canvasId,
        canvas,
        x: 0, y: 0,
        width: canvasWidth,
        height: canvasHeight,
        destWidth: canvasWidth * dpr,
        destHeight: canvasHeight * dpr,
        success: (res: any) => resolve(res.tempFilePath),
        fail: (err: any) => {
          console.error('canvasToTempFilePath 失败', err)
          reject(err)
        }
      } as any, componentInstance)
    }, 500)
  })
}

/**
 * 生成故事分享海报
 */
export interface StoryPosterConfig {
  title: string
  content: string
  imageUrl?: string
  location?: string
  date?: string
  weekday?: string
  moodIcon?: string
  weatherIcon?: string
  userName?: string
  cardBackground?: string
}

export async function generateStoryPoster(
  canvasId: string,
  config: StoryPosterConfig,
  componentInstance?: any
): Promise<string> {
  const systemInfo = uni.getSystemInfoSync()
  const canvasWidth = 600
  const canvasHeight = 900
  const dpr = systemInfo.pixelRatio || 2

  let canvasRes: any = null
  for (let attempt = 0; attempt < 3; attempt++) {
    const query = (uni.createSelectorQuery() as any).in(componentInstance)
    canvasRes = await new Promise<any>((resolve, reject) => {
      query.select(`#${canvasId}`)
        .fields({ node: true, size: true })
        .exec((res: any) => {
          if (res && res[0] && res[0].node) {
            resolve(res[0])
          } else {
            reject(new Error(`Canvas节点未找到(尝试${attempt + 1}/3)`))
          }
        })
    }).catch(() => null)
    if (canvasRes) break
    await new Promise(resolve => setTimeout(resolve, 300))
  }

  if (!canvasRes || !canvasRes.node) {
    throw new Error('Canvas节点未找到')
  }

  const canvas = canvasRes.node
  canvas.width = canvasWidth * dpr
  canvas.height = canvasHeight * dpr
  const ctx = canvas.getContext('2d') as CanvasRenderingContext2D
  ctx.scale(dpr, dpr)

  const textDark = '#1D1D1F'
  const textSecondary = '#86868B'

  // 渐变背景
  const bgGradient = ctx.createLinearGradient(0, 0, 0, canvasHeight)
  bgGradient.addColorStop(0, '#FFD89E')
  bgGradient.addColorStop(1, '#FFCB7C')
  ctx.fillStyle = bgGradient
  drawRoundRect(ctx, 0, 0, canvasWidth, canvasHeight, 24)
  ctx.fill()

  const padding = 48
  const contentWidth = canvasWidth - padding * 2
  let currentY = 56

  // 用户名
  ctx.fillStyle = textDark
  ctx.font = `600 28px -apple-system, "PingFang SC", sans-serif`
  ctx.fillText(config.userName || '旅行者', padding, currentY)
  currentY += 48

  // 分割线（虚线）
  ctx.save()
  ctx.setLineDash([8, 6])
  ctx.strokeStyle = 'rgba(0,0,0,0.15)'
  ctx.lineWidth = 2
  ctx.beginPath()
  ctx.moveTo(padding, currentY)
  ctx.lineTo(canvasWidth - padding, currentY)
  ctx.stroke()
  ctx.restore()
  currentY += 32

  // 白色内容卡片
  const cardX = padding
  const cardY = currentY
  const cardWidth = contentWidth
  const cardRadius = 20

  // 先计算卡片高度
  let cardContentY = cardY + 32

  // 标题
  cardContentY += 40

  // 图片
  let imageHeight = 0
  let storyImagePath = ''
  if (config.imageUrl) {
    try {
      storyImagePath = await downloadNetworkImage(config.imageUrl)
    } catch (e) {
      console.warn('故事图片下载失败', e)
    }
  }
  if (storyImagePath) {
    imageHeight = 240
    cardContentY += imageHeight + 20
  }

  // 日期行
  cardContentY += 36

  // 位置
  if (config.location) {
    cardContentY += 36
  }

  cardContentY += 16 // 底部padding

  // 绘制白色卡片背景
  ctx.fillStyle = 'rgba(255,255,255,0.65)'
  drawRoundRect(ctx, cardX, cardY, cardWidth, cardContentY - cardY, cardRadius)
  ctx.fill()

  // 重新绘制卡片内容
  let drawY = cardY + 32

  // 标题
  ctx.fillStyle = textDark
  ctx.font = `bold 32px -apple-system, "PingFang SC", sans-serif`
  ctx.textAlign = 'center'
  drawTextWithEllipsis(ctx, config.title || '旅行日记', cardX + cardWidth / 2, drawY, cardWidth - 48, 32, 'bold')
  ctx.textAlign = 'left'
  drawY += 48

  // 图片
  if (storyImagePath) {
    try {
      const image = canvas.createImage()
      await new Promise<void>((resolve, reject) => {
        image.onload = () => resolve()
        image.onerror = () => reject(new Error('图片加载失败'))
        image.src = storyImagePath
      })
      const imgPadding = 24
      const imgWidth = cardWidth - imgPadding * 2
      drawRoundRect(ctx, cardX + imgPadding, drawY, imgWidth, imageHeight, 12)
      ctx.save()
      ctx.clip()
      const imgRatio = image.width / image.height
      const targetRatio = imgWidth / imageHeight
      let sx = 0, sy = 0, sw = image.width, sh = image.height
      if (imgRatio > targetRatio) {
        sw = image.height * targetRatio
        sx = (image.width - sw) / 2
      } else {
        sh = image.width / targetRatio
        sy = (image.height - sh) / 2
      }
      ctx.drawImage(image, sx, sy, sw, sh, cardX + imgPadding, drawY, imgWidth, imageHeight)
      ctx.restore()
    } catch (e) {
      ctx.fillStyle = '#F0F0F5'
      drawRoundRect(ctx, cardX + 24, drawY, cardWidth - 48, imageHeight, 12)
      ctx.fill()
    }
    drawY += imageHeight + 20
  }

  // 日期和星期
  ctx.font = `600 28px -apple-system, "PingFang SC", sans-serif`
  ctx.fillStyle = textDark
  const dateText = config.date || ''
  ctx.fillText(dateText, cardX + 24, drawY)
  if (config.weekday) {
    const dateWidth = ctx.measureText(dateText).width
    ctx.font = `26px -apple-system, "PingFang SC", sans-serif`
    ctx.fillStyle = textSecondary
    ctx.fillText(config.weekday, cardX + 24 + dateWidth + 16, drawY)
  }
  drawY += 36

  // 位置
  if (config.location) {
    ctx.fillStyle = 'rgba(0,0,0,0.05)'
    drawRoundRect(ctx, cardX + 24, drawY - 20, ctx.measureText(config.location).width + 56, 36, 18)
    ctx.fill()
    ctx.font = `24px -apple-system, "PingFang SC", sans-serif`
    ctx.fillStyle = textSecondary
    ctx.fillText(`📍 ${config.location}`, cardX + 36, drawY)
    drawY += 36
  }

  currentY = cardContentY + 24

  // 内容摘要
  if (config.content) {
    ctx.fillStyle = textDark
    const firstChar = config.content.charAt(0)
    ctx.font = `bold 44px -apple-system, "PingFang SC", sans-serif`
    ctx.fillText(firstChar, padding, currentY + 8)
    const firstCharWidth = ctx.measureText(firstChar).width
    ctx.font = `26px -apple-system, "PingFang SC", sans-serif`
    ctx.fillStyle = textDark
    const remaining = config.content.slice(1, 100)
    drawTextWithEllipsis(ctx, remaining, padding + firstCharWidth + 4, currentY, contentWidth - firstCharWidth - 4, 26)
    currentY += 44
  }

  // 分割线
  currentY += 16
  ctx.save()
  ctx.setLineDash([8, 6])
  ctx.strokeStyle = 'rgba(0,0,0,0.15)'
  ctx.lineWidth = 2
  ctx.beginPath()
  ctx.moveTo(padding, currentY)
  ctx.lineTo(canvasWidth - padding, currentY)
  ctx.stroke()
  ctx.restore()
  currentY += 32

  // 底部品牌
  ctx.fillStyle = textDark
  ctx.font = `600 24px -apple-system, "PingFang SC", sans-serif`
  ctx.fillText('📖 趣途云迹', padding, currentY)
  ctx.fillStyle = textSecondary
  ctx.font = `24px -apple-system, "PingFang SC", sans-serif`
  ctx.fillText('丨旅行故事', padding + ctx.measureText('📖 趣途云迹').width + 4, currentY)

  ctx.textAlign = 'right'
  ctx.fillStyle = textSecondary
  ctx.font = `22px -apple-system, "PingFang SC", sans-serif`
  ctx.fillText('记录每一段旅途的美好', canvasWidth - padding, currentY)
  ctx.textAlign = 'left'

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      uni.canvasToTempFilePath({
        canvasId,
        canvas,
        x: 0, y: 0,
        width: canvasWidth,
        height: canvasHeight,
        destWidth: canvasWidth * dpr,
        destHeight: canvasHeight * dpr,
        success: (res: any) => resolve(res.tempFilePath),
        fail: (err: any) => {
          console.error('canvasToTempFilePath 失败', err)
          reject(err)
        }
      } as any, componentInstance)
    }, 500)
  })
}

export function trackShare(channel: string, path: string) {
  console.log(`[Share] channel=${channel}, path=${path}`)
  try {
    const shareLog = uni.getStorageSync('share_log') || []
    shareLog.push({
      channel,
      path,
      timestamp: Date.now()
    })
    const recentLogs = shareLog.slice(-50)
    uni.setStorageSync('share_log', recentLogs)
  } catch (e) {}
}
