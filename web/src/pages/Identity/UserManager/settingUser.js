import React, { Component } from 'react';
import { connect } from 'dva';
import {
  Icon,
  Button,
  Form,
  Upload,
  Select,
  Radio,
  Input,
} from 'antd';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

@connect(({ fabricUserManager }) => {
  return {
    fabricUserManager
  };
})
class SettingUser extends Component {
  constructor(props) {
    super(props);
    this.state = {
      tlsEnable: true,
      certFileList: [],
      keyFileList: [],
      clientFileList: [],
    };
  }

  componentDidMount() {
    const { updateList } = this.props.fabricUserManager;
    this.setState({
      tlsEnable: updateList.tlsEnable
    });
  }

  componentDidUpdate(prevProps, prevState) {
    const { form, fabricUserManager } = this.props;
    const { isFetching, updateList } = fabricUserManager;
    const oldFetching = prevProps.fabricUserManager.isFetching;
    const prevList = prevProps.fabricUserManager.updateList;

    if (oldFetching && !isFetching) {
      form.resetFields();
    }
    if (!prevList.id && updateList.id || prevList.id && !updateList.id) {
      this.setState({
        tlsEnable: updateList.tlsEnable
      });
    }
  }

  changeTlsEnable = (e) => {
    const value = e.target.value;
    this.setState({
      tlsEnable: value
    })
  }

  certChange = (info) => {
    let fileList = info.fileList;
    fileList = fileList.slice(-1);
    this.setState({
      certFileList: fileList
    })
  }

  keyChange = (info) => {
    let fileList = info.fileList;
    fileList = fileList.slice(-1);
    this.setState({
      keyFileList: fileList
    })
  }

  clientChange = (info) => {
    let fileList = info.fileList;
    fileList = fileList.slice(-1);
    this.setState({
      clientFileList: fileList
    })
  }

  beforeUpload = (file) => {
    return false;
  }
  // 提交表单
  handleSubmit = (e) => {
    e.preventDefault();
    const { form, dispatch, fabricUserManager } = this.props;

    form.validateFields((err, values) => {
      if (!err) {
        let formData = new FormData();
        Object.keys(values).forEach(key => {
          if (values[key] !== undefined) {
            formData.append(key, values[key] && values[key].file ? values[key].file : values[key])
          }
        });
        if (fabricUserManager.isUpdate) {
          dispatch({
            type: 'fabricUserManager/handleUpdateUser',
            payload: {
              data: formData,
              userId: values.userName
            }
          });
        } else {
          dispatch({
            type: 'fabricUserManager/handleSetUser',
            payload: formData
          });
        }
      }
    })
  }
  // 清空内容
  handleClean = () => {
    const { form, dispatch } = this.props;
    form.resetFields();
    dispatch({
      type: 'fabricUserManager/handlecleanUserList'
    });
  }

  render() {
    const { form, fabricUserManager } = this.props;
    const { isFetching, isUpdate, updateList } = fabricUserManager;
    const { getFieldDecorator } = form;
    const { certFileList, keyFileList, clientFileList, tlsEnable } = this.state;
    const formItemLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
    };

    return (
      <Form onSubmit={this.handleSubmit}>
        <FormItem {...formItemLayout} label="用户名称">
          {getFieldDecorator('userName', {
            initialValue: updateList.id,
            rules: [{
              required: true,
              message: '请输入用户名称！'
            }]
          })(<Input placeholder="请输入用户名称" />)}
        </FormItem>
        <FormItem {...formItemLayout} label="是否为admin用户">
          {getFieldDecorator('admin', {
            initialValue: !updateList.admin ? false : true,
          })(<RadioGroup>
            <Radio value={true}>是</Radio>
            <Radio value={false}>否</Radio>
          </RadioGroup>)}
        </FormItem>
        <FormItem {...formItemLayout} label="签名证书">
          {getFieldDecorator('signCerts', {
            rules: [{
              required: tlsEnable,
              message: '请上传签名证书！'
            }]
          })(
            <Upload
              name="signCerts"
              fileList={certFileList}
              onChange={this.certChange}
              beforeUpload={this.beforeUpload}
            >
              <Button>
                <Icon type="upload" />上传签名证书
              </Button>
            </Upload>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label="签名私钥">
          {getFieldDecorator('keyStore', {
            rules: [{
              required: tlsEnable,
              message: '请上传签名私钥！'
            }]
          })(
            <Upload
              name="keyStore"
              fileList={certFileList}
              onChange={this.certChange}
              beforeUpload={this.beforeUpload}
            >
              <Button>
                <Icon type="upload" />上传签名私钥
              </Button>
            </Upload>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label="是否使用tls">
          {getFieldDecorator('tlsEnable', {
            initialValue: !updateList.tlsEnable ? false : true,
          })(<RadioGroup onChange={this.changeTlsEnable}>
            <Radio value={true}>是</Radio>
            <Radio value={false}>否</Radio>
          </RadioGroup>)}
        </FormItem>
        <div style={{ display: tlsEnable ? 'block' : 'none' }}>
          <FormItem {...formItemLayout} label="tlsCA证书">
            {getFieldDecorator('tlsCa', {
              rules: [{
                required: tlsEnable,
                message: '请上传tls客户端证书！'
              }]
            })(
              <Upload
                name="tlsCa"
                fileList={certFileList}
                onChange={this.certChange}
                beforeUpload={this.beforeUpload}
              >
                <Button>
                  <Icon type="upload" /> 上传tls客户端证书
                </Button>
              </Upload>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="tls证书">
            {getFieldDecorator('tlsCert', {
              rules: [{
                required: tlsEnable,
                message: '请上传tls服务端证书'
              }]
            })(
              <Upload
                name="tlsCert"
                fileList={clientFileList}
                onChange={this.clientChange}
                beforeUpload={this.beforeUpload}
              >
                <Button>
                  <Icon type="upload" /> 上传tls服务端证书
                </Button>
              </Upload>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="tls私钥">
            {getFieldDecorator('tlsKey', {
              rules: [{
                required: tlsEnable,
                message: '请上传tls客户端私钥！'
              }]
            })(
              <Upload
                name="tlsKey"
                fileList={keyFileList}
                onChange={this.keyChange}
                beforeUpload={this.beforeUpload}
              >
                <Button>
                  <Icon type="upload" /> 上传tls客户端私钥
                </Button>
              </Upload>
            )}
          </FormItem>
        </div>

        <FormItem wrapperCol={{ span: 8, offset: 8 }}>
          <Button onClick={this.handleClean} style={{ marginRight: 10 }}>清空内容</Button>
          <Button type="primary" htmlType="submit" loading={isFetching}>{isUpdate ? "更新用户信息" : "新增用户"}</Button>
        </FormItem>
      </Form>
    )
  }
}

const WrapSettingUser = Form.create({})(SettingUser);
export default WrapSettingUser;
