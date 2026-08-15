# 01_Calculator デザイン書PDF生成スクリプト
# 
# 用法: ./generate-pdf.ps1
#
# 必要な環境:
#   - Ruby + asciidoctor-pdf
#   - asciidoctor-diagram
#   - PlantUML
#   - VL-Gothic フォント (../fonts/VL-Gothic-Regular.ttf)

# カレントディレクトリ確認
Write-Host "現在のディレクトリ: $(Get-Location)"

# キャッシュをクリア
Write-Host "キャッシュをクリア中..."
Remove-Item .asciidoctor -Recurse -Force -ErrorAction SilentlyContinue | Out-Null
Remove-Item design.pdf -ErrorAction SilentlyContinue | Out-Null

# UTF-8 エンコーディング設定（日本語対応）
$env:JRUBY_OPTS="-Dfile.encoding=UTF-8"

# PDF 生成コマンド
Write-Host "PDF を生成中..."
asciidoctor-pdf -r asciidoctor-diagram -a pdf-fontsdir=../fonts design.adoc

# 結果確認
if (Test-Path design.pdf) {
    $fileInfo = Get-Item design.pdf
    Write-Host "✓ PDF 生成成功！" -ForegroundColor Green
    Write-Host "  ファイル: $(Get-Item design.pdf | Select-Object -ExpandProperty FullName)"
    Write-Host "  サイズ: $($fileInfo.Length / 1KB)KB"
    Write-Host "  更新時刻: $($fileInfo.LastWriteTime)"
} else {
    Write-Host "✗ PDF 生成失敗！" -ForegroundColor Red
    exit 1
}
