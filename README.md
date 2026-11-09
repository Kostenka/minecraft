# 方向小幫手 Direction Helper（Fabric 1.21.11）

## 功能
1. **輸出方向箭頭**：準心對著投擲器、發射器、偵測器、漏斗、合成器時，輸出那一面外面會出現紅色粒子箭頭，
   快捷列上方顯示「輸出方向：下 ↓」「北（妳的左邊 ←）」之類的文字。偵測器另外用藍色短線標出偵測面。
2. **音階盒只有空手能調音**：手上拿任何東西右鍵音階盒，會直接放方塊／用物品，不用再蹲。
   在沒裝這個模組的伺服器上也有效（會在點擊那一瞬間自動幫妳送出「蹲下」）。

## 需要
- Minecraft 1.21.11 + Fabric Loader 0.18.1 以上 + Fabric API

## 用 GitHub 編譯（不用在電腦裝任何東西）
1. 在 GitHub 開一個新的 repository，把這個資料夾全部上傳（包含 `.github` 資料夾）。
2. 上傳完會自動開始編譯，到 repo 的 **Actions** 分頁，點最新那次 → 最下面 **Artifacts** 下載 `direction-helper-jar`。
3. 解壓縮，把 `direction-helper-1.0.0.jar`（不是 `-sources` 那個）放進 `.minecraft/mods`。

## 在自己電腦編譯
需要 JDK 21。在資料夾裡執行：
- Windows：`gradlew.bat build`
- Mac/Linux：`./gradlew build`
成品在 `build/libs/`。
