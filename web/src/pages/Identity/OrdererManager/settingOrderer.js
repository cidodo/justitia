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

@connect(({ ordererManager }) => {
  return {
    ordererManager
  };
})
class SettingOrderer extends Component {
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
    const { updateList } = this.props.ordererManager;
    this.setState({
      tlsEnable: updateList.tlsEnable
    });
  }

  componentDidUpdate(prevProps, prevState) {
    const { form, ordererManager } = this.props;
    const { isFetching, updateList } = ordererManager;
    const oldFetching = prevProps.ordererManager.isFetching;
    const prevList = prevProps.ordererManager.updateList;

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
    const { form, dispatch, ordererManager } = this.props;

    form.validateFields((err, values) => {
      if (!err) {
        let formData = new FormData();
        Object.keys(values).forEach(key => {
          if (values[key] !== undefined) {
            formData.append(key, values[key] && values[key].file ? values[key].file : values[key])
          }
        });
        if (ordererManager.isUpdate) {
          dispatch({
            type: 'ordererManager/handleUpdateOrderer',
            payload: {
              data: formData,
              ordererId: values.name
            }
          });
        } else {
          dispatch({
            type: 'ordererManager/handleSetOrderer',
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
      type: 'ordererManager/handlecleanOrdererList'
    });
  }

  render() {
    const { form, ordererManager } = this.props;
    const { isFetching, isUpdate, updateList } = ordererManager;
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
        <FormItem {...formItemLayout} label="节点名称">
          {getFieldDecorator('name', {
            initialValue: updateList.id,
            rules: [{
              required: true,
              message: '请输入节点名称！'
            }, {
              pattern: /^[0-9a-zA-Z_]{1,16}$/g, message: '请输入数字字母下划线组成的不超过16位字符的名称！'
            }]
          })(<Input placeholder="请输入数字字母下划线组成的不超过16位字符的名称" />)}
        </FormItem>
        <FormItem {...formItemLayout} label="端口">
          {getFieldDecorator('port', {
            initialValue: updateList.port,
            rules: [{
              required: true,
              message: '请输入端口！'
            }, {
              pattern: /^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-4]\d{4}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/,
              message: '输入的端口不正确！',
            }]
          })(<Input placeholder="请输入端口" />)}
        </FormItem>
        <FormItem {...formItemLayout} label="容器名称">
          {getFieldDecorator('containerId', {
            initialValue: updateList.containerId,
          })(
            <Input placeholder="请输入容器名称" />
          )}
        </FormItem>
        <FormItem {...formItemLayout} label="系统通道名称">
          {getFieldDecorator('systemChainId', {
            initialValue: updateList.systemChainId || 'testchainid',
          })(<Input placeholder="请输入系统通道" />)}
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
          <FormItem {...formItemLayout} label="是否使用tls双端校验">
            {getFieldDecorator('doubleVerify', {
              initialValue: !updateList.doubleVerify ? false : true,
            })(<RadioGroup>
              <Radio value={true}>是</Radio>
              <Radio value={false}>否</Radio>
            </RadioGroup>)}
          </FormItem>
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
          <Button type="primary" htmlType="submit" loading={isFetching}>{isUpdate ? "更新配置节点" : "配置Orderer节点"}</Button>
        </FormItem>
      </Form>
    )
  }
}

const WrapSettingOrderer = Form.create({})(SettingOrderer);
export default WrapSettingOrderer;
