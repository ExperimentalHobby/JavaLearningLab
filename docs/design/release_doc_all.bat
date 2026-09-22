@echo off
setlocal

pushd 01_Calculator
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 02_RockPaperScissors
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 03_UnitConverter
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 04_ToDoList
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 05_BmiTracker
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 06_RpnCalculator
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 07_ShapePolymorphism
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 08_RpsTournament
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 09_QuizApp
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 10_BankAccountSystem
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 11_HouseholdBudgetGui
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 12_StreamApiPractice
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 13_FileOrganizer
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 14_HttpClientTool
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 15_MultiThreadDownloader
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 16_JdbcCrudApp
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 17_GenericCollectionLib
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 18_TextEditorSwing
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 19_LogAnalyzerRegex
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 20_SpringBootApiIntro
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 21_MazeSolverVisualizer
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 22_GameOfLife
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 23_DesignPatternsPractice
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 24_SocketChatApp
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 25_JUnitPractice
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 26_BuilderPatternOrderApp
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 27_JpaHibernateApp
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 28_LoggingTool
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 29_CompletableFutureDemo
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 30_MavenCliPackaging
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 31_ModernJavaSyntax
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 32_BeanValidationApi
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 33_SpringSecurityAuth
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 36_JacksonJsonMapping
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 37_OptionalNullSafety
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 38_VirtualThreadsDemo
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 39_AlgorithmsDataStructures
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 40_ReflectionAnnotation
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 41_JpmsModuleDemo
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 42_WebSocketChat
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 43_JmhBenchmark
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd 44_JpaRestApi
powershell -ExecutionPolicy Bypass -File generate-pdf.ps1
popd

pushd release
if not exist 01_Calculator mkdir 01_Calculator
if not exist 02_RockPaperScissors mkdir 02_RockPaperScissors
if not exist 03_UnitConverter mkdir 03_UnitConverter
if not exist 04_ToDoList mkdir 04_ToDoList
if not exist 05_BmiTracker mkdir 05_BmiTracker
if not exist 06_RpnCalculator mkdir 06_RpnCalculator
if not exist 07_ShapePolymorphism mkdir 07_ShapePolymorphism
if not exist 08_RpsTournament mkdir 08_RpsTournament
if not exist 09_QuizApp mkdir 09_QuizApp
if not exist 10_BankAccountSystem mkdir 10_BankAccountSystem
if not exist 11_HouseholdBudgetGui mkdir 11_HouseholdBudgetGui
if not exist 12_StreamApiPractice mkdir 12_StreamApiPractice
if not exist 13_FileOrganizer mkdir 13_FileOrganizer
if not exist 14_HttpClientTool mkdir 14_HttpClientTool
if not exist 15_MultiThreadDownloader mkdir 15_MultiThreadDownloader
if not exist 16_JdbcCrudApp mkdir 16_JdbcCrudApp
if not exist 17_GenericCollectionLib mkdir 17_GenericCollectionLib
if not exist 18_TextEditorSwing mkdir 18_TextEditorSwing
if not exist 19_LogAnalyzerRegex mkdir 19_LogAnalyzerRegex
if not exist 20_SpringBootApiIntro mkdir 20_SpringBootApiIntro
if not exist 21_MazeSolverVisualizer mkdir 21_MazeSolverVisualizer
if not exist 22_GameOfLife mkdir 22_GameOfLife
if not exist 23_DesignPatternsPractice mkdir 23_DesignPatternsPractice
if not exist 24_SocketChatApp mkdir 24_SocketChatApp
if not exist 25_JUnitPractice mkdir 25_JUnitPractice
if not exist 26_BuilderPatternOrderApp mkdir 26_BuilderPatternOrderApp
if not exist 27_JpaHibernateApp mkdir 27_JpaHibernateApp
if not exist 28_LoggingTool mkdir 28_LoggingTool
if not exist 29_CompletableFutureDemo mkdir 29_CompletableFutureDemo
if not exist 30_MavenCliPackaging mkdir 30_MavenCliPackaging
if not exist 31_ModernJavaSyntax mkdir 31_ModernJavaSyntax
if not exist 32_BeanValidationApi mkdir 32_BeanValidationApi
if not exist 33_SpringSecurityAuth mkdir 33_SpringSecurityAuth
if not exist 36_JacksonJsonMapping mkdir 36_JacksonJsonMapping
if not exist 37_OptionalNullSafety mkdir 37_OptionalNullSafety
if not exist 38_VirtualThreadsDemo mkdir 38_VirtualThreadsDemo
if not exist 39_AlgorithmsDataStructures mkdir 39_AlgorithmsDataStructures
if not exist 40_ConcurrencyUtilities mkdir 40_ConcurrencyUtilities
if not exist 41_JpmsModuleDemo mkdir 41_JpmsModuleDemo
if not exist 42_WebSocketChat mkdir 42_WebSocketChat
if not exist 43_JmhBenchmark mkdir 43_JmhBenchmark
if not exist 44_JpaRestApi mkdir 44_JpaRestApi

copy ..\01_Calculator\design.pdf 01_Calculator\.
copy ..\02_RockPaperScissors\design.pdf 02_RockPaperScissors\.
copy ..\03_UnitConverter\design.pdf 03_UnitConverter\.
copy ..\04_ToDoList\design.pdf 04_ToDoList\.
copy ..\05_BmiTracker\design.pdf 05_BmiTracker\.
copy ..\06_RpnCalculator\design.pdf 06_RpnCalculator\.
copy ..\07_ShapePolymorphism\design.pdf 07_ShapePolymorphism\.
copy ..\08_RpsTournament\design.pdf 08_RpsTournament\.
copy ..\09_QuizApp\design.pdf 09_QuizApp\.
copy ..\10_BankAccountSystem\design.pdf 10_BankAccountSystem\.
copy ..\11_HouseholdBudgetGui\design.pdf 11_HouseholdBudgetGui\.
copy ..\12_StreamApiPractice\design.pdf 12_StreamApiPractice\.
copy ..\13_FileOrganizer\design.pdf 13_FileOrganizer\.
copy ..\14_HttpClientTool\design.pdf 14_HttpClientTool\.
copy ..\15_MultiThreadDownloader\design.pdf 15_MultiThreadDownloader\.
copy ..\16_JdbcCrudApp\design.pdf 16_JdbcCrudApp\.
copy ..\17_GenericCollectionLib\design.pdf 17_GenericCollectionLib\.
copy ..\18_TextEditorSwing\design.pdf 18_TextEditorSwing\.
copy ..\19_LogAnalyzerRegex\design.pdf 19_LogAnalyzerRegex\.
copy ..\20_SpringBootApiIntro\design.pdf 20_SpringBootApiIntro\.
copy ..\21_MazeSolverVisualizer\design.pdf 21_MazeSolverVisualizer\.
copy ..\22_GameOfLife\design.pdf 22_GameOfLife\.
copy ..\23_DesignPatternsPractice\design.pdf 23_DesignPatternsPractice\.
copy ..\24_SocketChatApp\design.pdf 24_SocketChatApp\.
copy ..\25_JUnitPractice\design.pdf 25_JUnitPractice\.
copy ..\26_BuilderPatternOrderApp\design.pdf 26_BuilderPatternOrderApp\.
copy ..\27_JpaHibernateApp\design.pdf 27_JpaHibernateApp\.
copy ..\28_LoggingTool\design.pdf 28_LoggingTool\.
copy ..\29_CompletableFutureDemo\design.pdf 29_CompletableFutureDemo\.
copy ..\30_MavenCliPackaging\design.pdf 30_MavenCliPackaging\.
copy ..\31_ModernJavaSyntax\design.pdf 31_ModernJavaSyntax\.
copy ..\32_BeanValidationApi\design.pdf 32_BeanValidationApi\.
copy ..\33_SpringSecurityAuth\design.pdf 33_SpringSecurityAuth\.
copy ..\36_JacksonJsonMapping\design.pdf 36_JacksonJsonMapping\.
copy ..\37_OptionalNullSafety\design.pdf 37_OptionalNullSafety\.
copy ..\38_VirtualThreadsDemo\design.pdf 38_VirtualThreadsDemo\.
copy ..\39_AlgorithmsDataStructures\design.pdf 39_AlgorithmsDataStructures\.
copy ..\40_ReflectionAnnotation\design.pdf 40_ReflectionAnnotation\.
copy ..\41_JpmsModuleDemo\design.pdf 41_JpmsModuleDemo\.
copy ..\42_WebSocketChat\design.pdf 42_WebSocketChat\.
copy ..\43_JmhBenchmark\design.pdf 43_JmhBenchmark\.
copy ..\44_JpaRestApi\design.pdf 44_JpaRestApi\.
popd

pause