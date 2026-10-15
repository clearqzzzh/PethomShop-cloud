package com.kk.pethome.basic.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class jsonresult {
        private Boolean success = true;
        private String message;
        private Object data;
        public jsonresult(Boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        public jsonresult(Object data) {
            this.data = data;
        }
        public static jsonresult success(Object data){
            return new jsonresult(data);
        }
        public static jsonresult fail(){
            return new jsonresult(false,"操作失败");
        }
        public static jsonresult fail(String message){
            return new jsonresult(false, message);
        }
        public jsonresult setMessage(String message) {
            this.message = message;
            return this;
        }

}
