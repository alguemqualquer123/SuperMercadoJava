; =========================================
; SuperMercado PDV - Inno Setup Script
; =========================================

#define AppName "SuperMercado PDV"
#define AppVersion "1.0.0"
#define AppPublisher "SuperMercado"
#define AppExeName "supermercado-pdv.exe"
#define AppJarName "supermercado-pdv-1.0.0.jar"
#define AppURL "http://localhost:8080"

[Setup]
AppId={{A9E8E2F4-5E4A-4C9D-9E21-123456789ABC}}
AppName={#AppName}
AppVersion={#AppVersion}
AppPublisher={#AppPublisher}
DefaultDirName={pf}\SuperMercadoPDV
DefaultGroupName=SuperMercado PDV
DisableProgramGroupPage=yes
OutputDir=output
OutputBaseFilename=SuperMercadoPDV_Setup
Compression=lzma
SolidCompression=yes
WizardStyle=modern
ArchitecturesAllowed=x64
ArchitecturesInstallIn64BitMode=x64

[Languages]
Name: "portuguese"; MessagesFile: "compiler:Languages\BrazilianPortuguese.isl"

[Tasks]
Name: "desktopicon"; Description: "Criar atalho na área de trabalho"; GroupDescription: "Atalhos:"; Flags: unchecked

[Files]
; Arquivos da aplicação
Source: "target\{#AppJarName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "dist\{#AppExeName}"; DestDir: "{app}"; Flags: ignoreversion

; JRE embutido (opcional)
; Source: "jre\*"; DestDir: "{app}\jre"; Flags: recursesubdirs createallsubdirs

[Icons]
Name: "{group}\SuperMercado PDV"; Filename: "{app}\{#AppExeName}"
Name: "{autodesktop}\SuperMercado PDV"; Filename: "{app}\{#AppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#AppExeName}"; Description: "Executar SuperMercado PDV"; Flags: nowait postinstall skipifsilent

[UninstallDelete]
Type: filesandordirs; Name: "{app}\logs"

[Code]
procedure InitializeWizard();
begin
  WizardForm.LicenseAcceptedRadio.Checked := True;
end;
