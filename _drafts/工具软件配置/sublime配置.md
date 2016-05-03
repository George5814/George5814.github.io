### 1. Sublime 3 安装package control
#### 1.1 自动安装
##### 1.1.1 使用ctrl+“`”或者menu | view | Show console 
##### 1.1.2 将下面代码复制到命令框
import urllib.request,os,hashlib; h = '2915d1851351e5ee549c20394736b442' + '8bc59f460fa1548d1514676163dafc88'; pf = 'Package Control.sublime-package'; ipp = sublime.installed_packages_path(); urllib.request.install_opener( urllib.request.build_opener( urllib.request.ProxyHandler()) ); by = urllib.request.urlopen( 'http://packagecontrol.io/' + pf.replace(' ', '%20')).read(); dh = hashlib.sha256(by).hexdigest(); print('Error validating download (got %s instead of %s), please try manual install' % (dh, h)) if dh != h else open(os.path.join( ipp, pf), 'wb' ).write(by)
	
#### 1.2 手动安装
	下载文件Package Control.sublime-package，将文件复制到C:\Users\${user}\AppData\Roaming\Sublime Text 3\Installed Packages目录下