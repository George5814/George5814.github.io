@echo off
rem 将该文件放在maven的.m2同级目录，执行后会删除没有更新完成的jar包
del /a /f /s /q *.lastUpdated
pause