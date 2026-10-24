param(
    [ValidatePattern('^[a-zA-Z0-9_]{3,100}$')][string]$Login = 'admin',
    [string]$Name = 'Administrator',
    [Security.SecureString]$AdminPassword
)
$ErrorActionPreference = 'Stop'
$secret = if ($AdminPassword) { $AdminPassword } else { Read-Host 'Пароль нового администратора (8–72 символа)' -AsSecureString }
$pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secret)
try { $password = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer) }
finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer) }
if ($password.Length -lt 8 -or $password.Length -gt 72) { throw 'Пароль должен содержать 8–72 символа.' }
$body = @{ name = $Name; login = $Login; password = $password } | ConvertTo-Json -Compress
try {
    Invoke-RestMethod -Uri 'http://127.0.0.1:8080/api/auth/register' -Method Post -ContentType 'application/json' -Body $body | Out-Null
    Write-Host 'Пользователь создан.'
} catch {
    if ($_.Exception.Response.StatusCode.value__ -ne 409) { throw }
    Write-Host 'Пользователь уже существует; пароль останется прежним.'
}
$updated = docker compose exec -T db psql -U postgres -d cinema_db -At -c "UPDATE user_data SET role = 'ADMIN' WHERE login = '$Login' RETURNING login;"
if ($LASTEXITCODE -ne 0 -or -not ($updated -contains $Login)) { throw 'Не удалось назначить роль администратора.' }
Write-Host "Администратор $Login готов. Войдите в JavaFX-клиент с этим логином и паролем."

