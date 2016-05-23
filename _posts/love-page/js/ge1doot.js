////////////////////////////////////////////////////////////
// ============= micro HTML5 library =====================
// @author Gerard Ferrandez / http://www.dhteumeuleu.com/
// last update: May 27, 2013
// Released under the MIT license
// http://www.dhteumeuleu.com/LICENSE.html
//主要设置了screen的大小和各种触摸和点击事件
////////////////////////////////////////////////////////////

// ===== ge1doot global =====

var ge1doot = ge1doot || {
	json: null,
	screen: null,
	pointer: null,
	camera: null,
	
	/**
	 * 
	 * @param {Object} url 动态引入的js文件url
	 * @param {Function} callback 回调方法
	 * @param {Object} data 数据参数
	 */
	loadJS: function (url, callback, data) {
		if (typeof url == "string") {//console.log(url);//url= "js/imageTransform3D.js"
			//将字符串的url转为数组
			url = [url];
		}
		/**
		 * 
		 * @param {String} src 元素src路径
		 */
		var load = function (src) {
			//创建script 元素
			var script = document.createElement("script");
				if (callback) {
					if (script.readyState){
						script.onreadystatechange = function () {
							if (script.readyState == "loaded" || script.readyState == "complete"){
								script.onreadystatechange = null;
								if (--n === 0) {
									callback(data || false);
								}
							}
						}
					} else {
						script.onload = function() {
							if (--n === 0) {
								callback(data || false);
							}
						}
					}
				}
				script.src = src;
				//将script脚本引入代码，添加到head元素
				document.getElementsByTagName("head")[0].appendChild(script);
		}
		for (var i = 0, n = url.length; i < n; i++){
			//加载js文件
			load(url[i]);
		} 
	}
}

// ===== html/canvas container =====

ge1doot.Screen = function (setup) {
	//当前屏幕
	ge1doot.screen = this;
	//获取指定的元素（canvas元素）
	this.elem = document.getElementById(setup.container) || setup.container;
	//如果是canvas元素，获取其2D的上下文
	this.ctx = this.elem.tagName == "CANVAS" ? this.elem.getContext("2d") : false;
	//获取canvas的样式
	this.style = this.elem.style;
	//设置屏幕的左边距和上边距，以及宽高为0
	this.left = 0;
	this.top = 0;
	this.width = 0;
	this.height = 0;
	//鼠标样式（小手指）
	this.cursor = "pointer";
	this.setup = setup;
	//根据canvas元素重新设置size
	this.resize = function () {
		var o = this.elem;
		this.width  = o.offsetWidth;
		this.height = o.offsetHeight;
		//o.offsetParent 当前元素最近的父元素(显示指定定位，然后各种偏移量(如:offsetLeft)是依据该元素计算的)
		for (this.left = 0, this.top = 0; o != null; o = o.offsetParent) {
			this.left += o.offsetLeft;
			this.top  += o.offsetTop;
		}
		if (this.ctx) {
			//设置canvas元素的width为offsetWidth
			this.elem.width  = this.width;
			this.elem.height = this.height;
		}
		//暂时注释，没有影响
//		this.setup.resize && this.setup.resize();
	}
	this.setCursor = function (type) {
		if (type !== this.cursor && 'ontouchstart' in window === false) {
			this.cursor = type;
			this.style.cursor = type;
		}
	}
	window.addEventListener('resize', function () {
		ge1doot.screen.resize();
	}, false);
	!this.setup.resize && this.resize();
}

// ==== unified touch events handler ====
//统一触摸事件管理器

ge1doot.Pointer = function (setup) {
	ge1doot.pointer = this;
	var self        = this;
	var body        = document.body;
	var html        = document.documentElement;
	this.setup      = setup;
	this.screen     = ge1doot.screen;
	this.elem       = this.screen.elem;
	this.X          = 0;
	this.Y          = 0;
	this.Xi         = 0;
	this.Yi         = 0;
	this.bXi        = 0;
	this.bYi        = 0;
	this.Xr         = 0;
	this.Yr         = 0;
	this.startX     = 0;
	this.startY     = 0;
	this.scale      = 0;//缩放
	this.wheelDelta = 0;//鼠标滚轮
	this.isDraging  = false;//拖拽
	this.hasMoved   = false;
	this.isDown     = false;
	this.evt        = false;
	var sX          = 0;
	var sY          = 0;
	// prevent default behavior
	if (setup.tap) this.elem.onclick = function () { return false; }
	if (!setup.documentMove) {
		document.ontouchmove = function(e) { e.preventDefault(); }
	}
	//禁止ms的提示事件
	document.addEventListener("MSHoldVisual", function(e) { e.preventDefault(); }, false);
	if (typeof this.elem.style.msTouchAction != 'undefined') this.elem.style.msTouchAction = "none";
	
	//向下
	this.pointerDown = function (e) {
		if (!this.isDown) {
			if (this.elem.setCapture) this.elem.setCapture();
			this.isDraging = false;
			this.hasMoved = false;
			this.isDown = true;
			this.evt = e;
			this.Xr = (e.clientX !== undefined ? e.clientX : e.touches[0].clientX);
			this.Yr = (e.clientY !== undefined ? e.clientY : e.touches[0].clientY);
			this.X  = sX = this.Xr - this.screen.left;
			this.Y  = sY = this.Yr - this.screen.top + ((html && html.scrollTop) || body.scrollTop);
			this.setup.down && this.setup.down(e);
		}
	}
	//移动
	this.pointerMove = function(e) {
		this.Xr = (e.clientX !== undefined ? e.clientX : e.touches[0].clientX);
		this.Yr = (e.clientY !== undefined ? e.clientY : e.touches[0].clientY);
		this.X  = this.Xr - this.screen.left;
		this.Y  = this.Yr - this.screen.top + ((html && html.scrollTop) || body.scrollTop);
		if (this.isDown) {
			this.Xi = this.bXi + (this.X - sX);
			this.Yi = this.bYi - (this.Y - sY);
		}
		if (Math.abs(this.X - sX) > 11 || Math.abs(this.Y - sY) > 11) {
			this.hasMoved = true;
			if (this.isDown) {
				if (!this.isDraging) {
					this.startX = sX;
					this.startY = sY;
					this.setup.startDrag && this.setup.startDrag(e);
					this.isDraging = true;
				} else {
					this.setup.drag && this.setup.drag(e);
				}
			} else {
				sX = this.X;
				sY = this.Y;
			}
		}
		this.setup.move && this.setup.move(e);
	}
	//向上
	this.pointerUp = function(e) {
		this.bXi = this.Xi;
		this.bYi = this.Yi;
		if (!this.hasMoved) {
			this.X = sX;
			this.Y = sY;
			this.setup.tap && this.setup.tap(this.evt);
		} else {
			this.setup.up && this.setup.up(this.evt);
		}
		this.isDraging = false;
		this.isDown = false;
		this.hasMoved = false;
		this.setup.up && this.setup.up(this.evt);
		if (this.elem.releaseCapture) this.elem.releaseCapture();
		this.evt = false;
	}
	//取消
	this.pointerCancel = function(e) {
		if (this.elem.releaseCapture) this.elem.releaseCapture();
		this.isDraging = false;
		this.hasMoved = false;
		this.isDown = false;
		this.bXi = this.Xi;
		this.bYi = this.Yi;
		sX = 0;
		sY = 0;
	}
	//按下手指
	if ('ontouchstart' in window) {
		this.elem.ontouchstart      = function (e) { self.pointerDown(e); return false;  }
		this.elem.ontouchmove       = function (e) { self.pointerMove(e); return false;  }
		this.elem.ontouchend        = function (e) { self.pointerUp(e); return false;    }
		this.elem.ontouchcancel     = function (e) { self.pointerCancel(e); return false;}
	}
	//鼠标向下重置为触摸
	document.addEventListener("mousedown", function (e) {
		if (
			e.target === self.elem || 
			(e.target.parentNode && e.target.parentNode === self.elem) || 
			(e.target.parentNode.parentNode && e.target.parentNode.parentNode === self.elem)
		) {
			if (typeof e.stopPropagation != "undefined") {
				e.stopPropagation();
			} else {
				e.cancelBubble = true;
			}
			e.preventDefault();
			self.pointerDown(e); 
		}
	}, false);
	//鼠标向上重置为触摸移动
	document.addEventListener("mousemove", function (e) { self.pointerMove(e); }, false);
	//鼠标向上重置为放弃触摸
	document.addEventListener("mouseup",   function (e) { self.pointerUp(e); }, false);
	//当有多个触点，并有触点移动时
	document.addEventListener('gesturechange', function(e) {
		e.preventDefault();
		//多个触点时改变缩放
		if (e.scale > 1) {
			self.scale = 0.1; 
		}else if (e.scale < 1) {
			self.scale = -0.1; 
		}else {
			self.scale = 0;
		}
		self.setup.scale && self.setup.scale(e);
		return false;
	}, false);
	//检测浏览器是否支持ms的触控事件
	if (window.navigator.msPointerEnabled) {
		var nContact = 0;
		var myGesture = new MSGesture();
		myGesture.target = this.elem;
		//触摸
		this.elem.addEventListener("MSPointerDown", function(e) {
			if (e.pointerType == e.MSPOINTER_TYPE_TOUCH) {
				myGesture.addPointer(e.pointerId);
				nContact++;
			}
		}, false);
		//离开（放弃触摸）
		this.elem.addEventListener("MSPointerOut", function(e) {
			if (e.pointerType == e.MSPOINTER_TYPE_TOUCH) {
				nContact--;
			}
		}, false);
		//
		this.elem.addEventListener("MSGestureHold", function(e) {
			e.preventDefault();
		}, false);
		//多点触摸，并有触摸点移动
		this.elem.addEventListener("MSGestureChange", function(e) {
			if (nContact > 1) {
				if (e.preventDefault) e.preventDefault(); 
				self.scale = e.velocityExpansion;
				self.setup.scale && self.setup.scale(e);
			}
			return false;
		}, false);
	}
	if (window.addEventListener) this.elem.addEventListener('DOMMouseScroll', function(e) { 
		//禁止鼠标滚动的默认事件
		if (e.preventDefault) {
			e.preventDefault();
		}
		self.wheelDelta = e.detail * 10;
		self.setup.wheel && self.setup.wheel(e);
		return false; 
	}, false); 
	//鼠标滚轮事件
	this.elem.onmousewheel = function () { 
		self.wheelDelta = -event.wheelDelta * .25;
		self.setup.wheel && self.setup.wheel(event);
		return false; 
	}
}
// ===== request animation frame =====
//缓动函数,立即执行函数
window.requestAnimFrame = (function(){
	//返回缓动函数(兼容所有五大浏览器)，如果都不支持则使用setTimeout方法
		return  window.requestAnimationFrame || 
		window.webkitRequestAnimationFrame   || 
		window.mozRequestAnimationFrame      || 
		window.oRequestAnimationFrame        || 
		window.msRequestAnimationFrame       || 
		function( run ){
			window.setTimeout(run, 16);
		};
})();
