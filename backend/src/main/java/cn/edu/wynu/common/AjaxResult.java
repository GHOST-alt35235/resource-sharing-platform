package cn.edu.wynu.common;

public class AjaxResult {
    private int code;
    private String message;
    private Object data;

    public static AjaxResult success() {
        return success("操作成功");
    }

    public static AjaxResult success(String message) {
        AjaxResult result = new AjaxResult();
        result.setCode(HttpStatus.SUCCESS);
        result.setMessage(message);
        return result;
    }

    public static AjaxResult success(Object data) {
        AjaxResult result = new AjaxResult();
        result.setCode(HttpStatus.SUCCESS);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static AjaxResult success(String message, Object data) {
        AjaxResult result = new AjaxResult();
        result.setCode(HttpStatus.SUCCESS);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static AjaxResult error(String message) {
        AjaxResult result = new AjaxResult();
        result.setCode(HttpStatus.ERROR);
        result.setMessage(message);
        return result;
    }

    public static AjaxResult error(int code, String message) {
        AjaxResult result = new AjaxResult();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
