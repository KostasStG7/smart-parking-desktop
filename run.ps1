$ErrorActionPreference = "Stop"
Push-Location $PSScriptRoot
try {
    if (-not (Test-Path "config.local.ps1")) {
        throw "Copy config.example.ps1 to config.local.ps1 and configure MySQL first."
    }
    . ./config.local.ps1
    if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
        throw "JDK required: add javac and java to PATH."
    }
    $sources = @(Get-ChildItem -Path src -Recurse -Filter *.java | Select-Object -ExpandProperty FullName)
    New-Item -ItemType Directory -Force out | Out-Null
    & javac -encoding UTF-8 -cp "lib/*" -d out $sources
    if ($LASTEXITCODE -ne 0) { throw "Compilation failed." }
    & java -cp "out;lib/*" main.Main
    if ($LASTEXITCODE -ne 0) { throw "Application exited with an error." }
}
finally { Pop-Location }
