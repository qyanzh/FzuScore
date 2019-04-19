import functools
import pymysql
from . import api
from flask import request, g, session
from app.db import get_db
from werkzeug.security import generate_password_hash, check_password_hash
import json
from .aes import aes
from Crypto.Cipher import AES
import decimal


class DecimalEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, decimal.Decimal):
            return float(o)
        super(DecimalEncoder, self).default(o)

def _response_data(is_success=0, message=None, data=None):
    response_data = {
        "is_success": is_success,
        "message": message,
        "data": data
    }
    return json.dumps(response_data, cls=DecimalEncoder, ensure_ascii=False)


def student_stat(student_id):
    db = get_db()
    cursor = db.cursor(pymysql.cursors.DictCursor)
    # student_id = g.user['student_id']
    cursor.execute(
        "SELECT student_term_start, student_term_end, student_name, student_permission FROM students WHERE student_id = %s"%student_id
    )
    student = cursor.fetchone()
    is_monitor = 0
    if student['student_permission'] >= 100:
        is_monitor = 1

    student_json = {
        "student_name": student['student_name'],
        "is_monitor": is_monitor
    }
    return student_json


@api.route('/login', methods=['GET', 'POST'])
def login():

    if request.method == 'POST':
        error = None
        data = json.loads(request.get_data(as_text=True))
        student_id = int(data['student_id'])
        try:
            password = pymysql.escape_string(aes.decrypt(data['student_password']))
        except:
            return _response_data(message="密码解析错误。")

        db = get_db()
        cursor = db.cursor(pymysql.cursors.DictCursor)
        if error is None:
            cursor.execute(
                "SELECT * FROM students WHERE student_id = %s" % student_id
            )
            student = cursor.fetchone()

            if student is None:
                return _response_data(message='该学号不存在。')
            elif not check_password_hash(student['student_password'], password):
                return _response_data(message='密码错误。')

        if error is None:
            return _response_data(message="登陆成功",is_success=1,data=student_stat(student_id))
    return _response_data()



@api.route('/change_password', methods=['POST'])
def change_password():
    data = json.loads(request.get_data(as_text=True))
    student_id = data['student_id']

    try:
        password_old = pymysql.escape_string(aes.decrypt(data['password_old']))
        password_new = pymysql.escape_string(aes.decrypt(data['password_new']))
    except:
        return _response_data(message="密码解析错误。")

    db = get_db()
    cursor = db.cursor(pymysql.cursors.DictCursor)

    cursor.execute("""
        SELECT student_password FROM students
        WHERE student_id = %s
    """ % student_id)

    password_old_in_db = cursor.fetchone()['student_password']
    if password_old_in_db is None:
        return _response_data(message="学号不存在")
    elif check_password_hash(password_old_in_db, password_old):
        cursor.execute('UPDATE students SET student_password="%s"WHERE student_id = %s'
                       % (generate_password_hash(password_new), student_id))
        db.commit()
        return _response_data(is_success=1, message="修改成功。")
    else:
        return _response_data(is_success=1, message="密码错误。")


    return _response_data()

# def student_login_required(view):
#     @functools.wraps(view)
#     def wrapped_view(**kwargs):
#         if g.user is None:
#             response_data = {
#                 'is_success': 0,
#                 'message': '请登录'
#             }
#             return json.dumps(response_data)
#         return view(**kwargs)
#     return wrapped_view
# @api.before_app_request
# def load_logged_in_user():
#     student_id = session.get('student_id')
#     if student_id is None:
#         g.user = None
#     else:
#         cursor = get_db().cursor(pymysql.cursors.DictCursor)
#         cursor.execute(
#             "SELECT * FROM students WHERE student_id = %s" % student_id
#         )
#         g.user = cursor.fetchone()
# @api.route('/register', methods=['GET', 'POST'])
# def register():
#     response_data = {
#         'is_success': 0,
#         'message': '',
#         'data': None
#     }
#     if request.method == 'POST':
#         data = json.loads(request.get_data(as_text=True))
#
#         student_id = int(data['student_id'])
#         password = pymysql.escape_string(data['student_password'])
#         student_name = pymysql.escape_string(data['student_name'])
#         term_start = data['term_start']
#         term_end = data['term_end']
#         db = get_db()
#         cursor = db.cursor(pymysql.cursors.DictCursor)
#         error = None
#         student = None
#
#         cursor.execute(
#                 "SELECT * FROM students WHERE student_id = %s" % student_id
#             )
#         student = cursor.fetchone()
#         if student is not None:
#             error = '该学号已存在'
#
#         if error is None:
#             cursor.execute(
#                 "INSERT INTO students(student_id, student_name, student_password, student_term_start, student_term_end)"
#                 " VALUES ('%s','%s','%s', '%s', '%s')" %
#                 (student_id, student_name, pymysql.escape_string(generate_password_hash(password)), term_start, term_end)
#             )
#             db.commit()
#             response_data['is_success'] = 1
#         response_data['message'] = error
#
#     return json.dumps(response_data, ensure_ascii=False)
#
#
#
# @api.route('/logout')
# def logout():
#     session.clear()
#     response_data = {
#         'is_success': 1,
#         'message': '登出成功'
#     }
#     return json.dumps(response_data)
