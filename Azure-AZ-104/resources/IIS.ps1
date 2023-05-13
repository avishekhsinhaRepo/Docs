import-module servermanager
add-windowsfeature web-server -includeallsubfeature
set-content -path "C:\inetpub\wwwroot\Default.html" -Value "<h1>Sever name: $($env:computername)</h1>"