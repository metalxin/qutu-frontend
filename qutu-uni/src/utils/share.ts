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
  return `/pages/destination/detail?id=${id}&from=share`
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

function trackShare(channel: string, path: string) {
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
