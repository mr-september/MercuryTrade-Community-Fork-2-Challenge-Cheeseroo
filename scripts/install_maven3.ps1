# Check for UAC elevation
if (-NOT ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator"))
{
    Write-Host "The script requires elevated permissions to run. Please run as administrator."
    Write-Host "Press any key to exit."
    [System.Console]::ReadKey($true)
    exit
}

# Download and install the latest version of Maven
$lines = Invoke-WebRequest -Uri "https://maven.apache.org/download.cgi" -UseBasicParsing
foreach ($line in $lines) {
    if ($line -match 'maven') {
        if ($line -match '-bin.zip') {
            $version = ($line -split '<td><a href="https://dlcdn.apache.org/maven/maven-3/')[1] -split '-bin.zip'
            $version = $version[0].TrimEnd('/">').Split('/')[0]
            write-host "$version"
            break
        }
    }
}

Write-Host "Latest version of Maven: $version"
$file = "apache-maven-$version-bin.zip"
$url = "https://dlcdn.apache.org/maven/maven-3/$version/binaries/$file"

Write-Host "Downloading $file"
Invoke-WebRequest -Uri $url -OutFile $file

Write-Host "Expanding $file"
Expand-Archive -Path $file -DestinationPath "C:\Program Files"

Write-Host "Cleaning up $file"
Remove-Item $file

Write-Host "Adding Maven to PATH environment variable"
[Environment]::SetEnvironmentVariable("Path", "$env:Path;C:\Program Files\apache-maven-$version\bin", [EnvironmentVariableTarget]::Machine)
$env:Path = [Environment]::GetEnvironmentVariable("Path", "Machine") + ";C:\Program Files\apache-maven-$version\bin"

Write-Host "Maven installation complete"
Write-Host "Manven version:"
mvn -version
