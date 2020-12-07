package com.gyx.nullmenuedittext

import android.content.Context
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText

class NullMenuEditText:AppCompatEditText {

	constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet){
		//禁止长按
		isLongClickable = false
		//禁止文本选中
		setTextIsSelectable(false)
		//EditText在横屏的时候会出现一个新的编辑界面，因此要禁止掉这个新的编辑界面；
		//新的编辑界面里有复制粘贴等功能按钮，目前测试是无效果的，以防外一，建议禁止掉。
		imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
		//用户选择操作无效化处理
		customSelectionActionModeCallback = object : ActionMode.Callback {
			override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
				return false
			}

			override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
				return false
			}

			override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
				return false
			}

			override fun onDestroyActionMode(mode: ActionMode) {}
		}

		setOnTouchListener { v, event ->
			if (event.action == MotionEvent.ACTION_DOWN) {
				// setInsertionDisabled when user touches the view
				setInsertionDisabled()
			}
			false
		}
	}

	/**
	 * 小米/OPPO手机上禁止复制粘贴功能
	 * 反射 android.widget.Editor 修改弹框菜单不显示
	 */
	private fun setInsertionDisabled() {
		try {
			val editorField = TextView::class.java.getDeclaredField("mEditor")
			editorField.isAccessible = true
			val editorObject = editorField[this]
			val editorClass = Class.forName("android.widget.Editor")
			val mInsertionControllerEnabledField = editorClass.getDeclaredField("mInsertionControllerEnabled")
			mInsertionControllerEnabledField.isAccessible = true
			mInsertionControllerEnabledField[editorObject] = false
			val mSelectionControllerEnabledField = editorClass.getDeclaredField("mSelectionControllerEnabled")
			mSelectionControllerEnabledField.isAccessible = true
			mSelectionControllerEnabledField[editorObject] = false
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	override fun onTextContextMenuItem(id: Int): Boolean {
		return true
	}

	override fun isSuggestionsEnabled(): Boolean {
		return false
	}


}